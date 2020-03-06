package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.model.File_Content;
import com.example.File_Parser.model.File_Tracking;



@RestController
public class FileContentController {
	@Autowired
	private FileContentService service;
	
	@PostMapping("/store-in-db")
	@ResponseBody
	public File_Tracking saveFileContent(String filepath ) throws IOException {
		/*
		 * 1. Reads a file, stores its name which will be the partition key of our table. 
		 * 2. Scans in chunks, 10mb and stores that in our buffer until used. 
		 * 3. The delimiter here, being a ".". The paragraphs are split into lines which are stored in the table.
		 * 4. Also has a separate table 'file_tracking' to keep track of the status of upload to database.
		 * 5. In case the upload fails, file_tracking will resume from a checkpoint.
		 */
	
		List<File_Content> file_content = new ArrayList<>();
		
		// For line counts, lets use an Atomic Integer. 
		int count = 0;
		
		/*
		 * Fail proof scanning wherein, even if a paragprah exceeds the size of memory available in the 
		 * local system, will not affect the process.
		 * Achieved using scanner which scans in 10Mb chunks. Splits the paragraphs based on the 
		 * occurrence of "." and stores in the table.
		 */

		Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(filepath)), 100*1024));
		
		/*
		 * Status - Status of the file upload to the database. Can either be "done" or "not done yet". 
		 * 			=> "done" status would mean lines are all stored in the table successfully.
		 * 			=> "not done yet" status would mean part of the file is yet to be stored/uploaded to the
		 * 			database.
		 * continue_from - This will be the checkpoint. If the process is stopped, next time the application is 
		 * 					made to run, upload will start from the checkpoint. 
		 */
		
		String status = "";
		int continue_from = 0;
		int id = 0;
		
		try {
		// A delimiter which will serve as identifying parts of a paragraph into lines.
			sc.useDelimiter("\\.");
			
			/*
			 * fileTrackingInit of the service layer is used to initialize id, status and continue_from
			 * long time = System.nanoTime() - start; 
			 */
			
			File_Tracking entry = service.getfileTrackingStatus(filepath);
			id = entry.getId();
			status = entry.getStatus();
			continue_from = entry.getCheckpointLine();
			
			/*
			 * If status is "done", that would mean that particular file was stored successfully and no need to
			 * proceed further.
			 */
			
			if(!(status.equals("done"))) {
				while(sc.hasNext()) {
					
					
				    String line = sc.next();
				    count += 1;
				    
				    // Continue from the checkpoint. 
					if(count > continue_from) {
	
						// Storing the above arraylist comprising of file_name, line_count and the line into the table.
						file_content.add(new File_Content(filepath,count,line)); 
					    
					    if(count % 10000==0) {
					    // Storing in batches of size 10000 (sentences).
					    
						    service.fileSentenceInsert(file_content);
						    // Also update the file_tracking column reflecting the latest checkpoint and the status. 
						    
						    service.fileTrackingUpdate(id, count, "Not done yet");
						    file_content.clear(); 
						    continue_from = count;
					    }
				    }
				}
				
				/*
				 * To store remaining content and also if the total number of lines was lesser than 10000:
				 */
				if(file_content.size()>0) {
					service.fileSentenceInsert(file_content);
				}
				// Final status to Done.
				service.fileTrackingUpdate(id, count, "done");
				}
			else 
			{ 
				System.out.println("already done!");
			}
			
		}
		finally {
			
			sc.close();
		}
		
		return service.getfileTrackingStatus(filepath);
		
	}
	
	@GetMapping("/getStatus/{id}")
	public File_Tracking getTrackStatus(@PathVariable("id") int id) {
		return service.getFileStatusByID(id);
	}
	

}