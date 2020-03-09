package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.stereotype.Service;

import com.example.File_Parser.model.File_Content;
import com.example.File_Parser.model.File_Tracking;
import com.example.File_Parser.repository.FileContentRepository;
import com.example.File_Parser.repository.FileTrackingRepository;


@Service
public class FileContentService {
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingRepository track_repository;
	
	public void fileSentenceInsert(List<File_Content> file_content) {
		repository.saveAll(file_content);
	}
	
	public int fileTrackInsert(File_Tracking file_tracking) {
		track_repository.save(file_tracking);
		return file_tracking.getId();
	}
	
	public File_Tracking fileTrackFindByFileName(String filename) {
		return track_repository.findByFilename(filename);
	}
	
	public void fileTrackingUpdate(int id, int count, String status) {
		/*
		 * Updating the tracking table.
		 */
		File_Tracking entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
	    entry.setStatus(status);
	    track_repository.save(entry);
	}
	
	
	public File_Tracking getFileStatusByID(int id) {
		return track_repository.findById(id);
	}
	
	public String getFileName(String filePath) {
		String[] filePathSplit = filePath.split("\\\\");
		return filePathSplit[filePathSplit.length-1];
	}
	
	public File_Tracking getfileTrackingStatus(String filename) {
		/*
		 * If ID of the file (hash value) is already present in our file_tracking table,
			 * then we would just get hold of the "status" and the "checkpoint" columns of our table.
			 * 
			 * Else, we would make a new entry in the file_tracking table and initialize status to "not done yet"
			 * and checkpoint to 0.
			 * 
			 * Returns the instance.
		 */
		if(track_repository.findByFilename(filename)==null) {
			File_Tracking entry = new File_Tracking(filename,0,"Not done yet");
			fileTrackInsert(entry);
			return entry;
			
		}
		else {
			File_Tracking actualEntity = track_repository.findByFilename(filename);
			return actualEntity;
		}
	}
	
	
	
	
	public void ParseFile(String filepath, int id, String status, int continue_from) {
		
		
		List<File_Content> file_content = new ArrayList<>(10000);
		int count = 0;
		try {
		Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(filepath)), 100*1024));
		String fileName = getFileName(filepath);
		System.out.println(fileName);
		
			// A delimiter which will serve as identifying parts of a paragraph into lines.
				sc.useDelimiter("\\.");
				
				/*
				 * fileTrackingInit of the service layer is used to initialize id, status and continue_from
				 * long time = System.nanoTime() - start; 
				 */
				
				/*
				 * If status is "done", that would mean that particular file was stored successfully and no need to
				 * proceed further.
				 */
				
				if(!(status.equals("done"))) {
					int new_count = 0;
					while(sc.hasNext()) {
						
						
					    String line = sc.next();
					    count += 1;
					    
					    // Continue from the checkpoint. 
						if(count > continue_from) {
							
							// Storing the above arraylist comprising of file_name, line_count and the line into the table.
				
							
							file_content.add(new File_Content(fileName,count,line)); 	
							
						    if(count % 10000==0) {
						    	
						    	// Storing in batches of size 10000 (sentences).
						    	
						    	
						    	long start = System.nanoTime();
							    fileSentenceInsert(file_content);
							    long time = System.nanoTime() - start;
							    System.out.println(time/1000000000);
							    // Also update the file_tracking column reflecting the latest checkpoint and the status. 
							    fileTrackingUpdate(id, count, "Not done yet");
							    
							    file_content.clear(); 
							    continue_from = count;
							    
							    
						    }
					    }
					}
					
					/*
					 * To store remaining content and also if the total number of lines was lesser than 10000:
					 */
					if(file_content.size()>0) {
						fileSentenceInsert(file_content);
					}
					// Final status to Done.
					fileTrackingUpdate(id, count, "done");
					}
				else 
				{ 
					System.out.println("already done!");
				}
				
				sc.close();
			}
		catch (FileNotFoundException e) {
			e.getStackTrace();
			// TODO: handle exception
		}
			
		
	}
	
	

}
