package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.Counter;
import com.example.demo.model.File_Content;
import com.example.demo.model.File_Tracking;
import com.example.demo.repository.FileContentRepository;
import com.example.demo.repository.FileTrackingRepository;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;


@RestController
public class FileContentController {
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingRepository track_repository;
	
	
	@PostMapping("/store-in-db")
	@ResponseBody
	public void saveFileContent(String dir, String filename ) throws IOException {
		/*
		 * 1. Reads a file, stores its name which will be the partition key of our table. 
		 * 2. Scans in chunks, 10mb and stores that in our buffer until used. 
		 * 3. The delimiter here, being a ".". The paragraphs are split into lines which are stored in the table.
		 * 4. Also has a separate table 'file_tracking' to keep track of the status of upload to database.
		 * 5. In case the upload fails, file_tracking will resume from a checkpoint.
		 */
	
		List<File_Content> file_content = new ArrayList<>();
		List<File_Tracking> file_tracking = new ArrayList<>();
		
		// For line counts, lets use an Atomic Integer. 
		AtomicInteger count = new AtomicInteger(0);
		
		/*
		 * Fail proof scanning wherein, even if a paragprah exceeds the size of memory available in the 
		 * local system, will not affect the process.
		 * Achieved using scanner which scans in chunks every 10Mb. Splits the paragraphs based on the 
		 * occurrence of "." and stores in the table.
		 * 
		 */
		String filepath = dir+filename;
		Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(filepath)), 10*1024));
		
		/*
		 * To keep track of a file, our file_tracking table requires a Unique Identifier. 
		 * Hash of the file name will be the unique Id (primary key of the file_tracking table).
		 */
		
		
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
			 * If ID of the file (hash value) is already present in our file_tracking table,
			 * then we would just get hold of the "status" and the "checkpoint" columns of our table.
			 * 
			 * Else, we would make a new entry in the file_tracking table and initialize status to "not done yet"
			 * and checkpoint to 0.
			 * 
			 */
			if((track_repository.findByFilename(filename)==null)) {
				System.out.println(filename);
				track_repository.save(new File_Tracking(filename, 0, "not done yet"));
				id = track_repository.findByFilename(filename).getId();
			}else {
				
				File_Tracking actualEntity = track_repository.findByFilename(filename);
				// if exists, update status and continue_from.
				status = actualEntity.getStatus();
				continue_from = actualEntity.getCheckpointLine();
				id = actualEntity.getId();
			}
			
			/*
			 * If status is "done", that would mean that particular file was stored successfully and no need to
			 * proceed further.
			 */
			
			if(!(status.equals("done"))) {
				while(sc.hasNext()) {
					
					
				    String line = sc.next();
				    count.incrementAndGet();
				    
				    // Continue from the checkpoint. 
					if(count.intValue() > continue_from) {
						
						
						// Storing the above arraylist comprising of file_name, line_count and the line into the table.
						file_content.add(new File_Content(filename,count.intValue(),line)); 
					    
					    // Storing in batches of size 10000 (sentences).
					    if(count.intValue() %10000==0) {
					    repository.saveAll(file_content);
					    
					    // Also update the file_tracking column reflecting the latest checkpoint and the status. 
					    
					    File_Tracking entry = track_repository.findById(id);
					    System.out.println(entry.getFilename());
					    
					    entry.setCheckpointLine(count.intValue());
					    entry.setStatus("Not done yet");
					    track_repository.save(entry);
//					    file_tracking.add(new File_Tracking(uniqueId, filename, count.intValue(), "Not done yet"));
//					    track_repository.saveAll(file_tracking);
					    
					    // Clear the arraylist after current batch is stored.
					    file_content.clear(); 
					    continue_from = count.intValue();
					    }
				    }
				}
				
				/*
				 * To store remaining content and also if the total number of lines was lesser than 10000:
				 */
				if(file_content.size()>0) {
					repository.saveAll(file_content);
				}
				File_Tracking entry = track_repository.findById(id);
			    
			    entry.setCheckpointLine(count.intValue());
			    entry.setStatus("done");
			    track_repository.save(entry);
				// Finally, lets change  the status to "done" and also update the checkpoint value to final line.
//				file_tracking.add(new File_Tracking(uniqueId, filename, count.intValue(), "done"));
//				track_repository.saveAll(file_tracking);
				}
			else 
			{ 
				System.out.println("already done!");
			}
		}
		finally {
			
			sc.close();
		}
		
//		return track_repository.findById(uniqueId);
	}
	
//	@GetMapping("/trackingDetails")
////	public List<File_Tracking> getFile_Tracking(){
//////		return track_repository.findAll();
////	}
//	
//	@GetMapping("/trackingDetails/{filename}")
//	public Optional<File_Tracking> getFile_TrackingById(@PathVariable String filename){
//		int id = hashValueOfFileName(filename);
//		return track_repository.findById(id);
//	}
//	
}
