package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.File_Parser.exception.RangeOutOfBounds;
import com.example.File_Parser.model.FileTrackStatus;
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
	
	int batch_size;
	int retrieve_size;
	String file_name;
	
	public void initializeVars(String file_name, int batch_size, int retrieve_size) {
		/*
		 * Initializing batch_size, file_name and retrieve size.
		 */
		this.batch_size = batch_size;
		this.file_name = file_name;
		this.retrieve_size = retrieve_size;
	}
	/*
	 * Misc methods
	 */
		public Scanner scanFile (String filepath) throws FileNotFoundException {
			/*
			 * Returns a new Scanner object from the filepath provided. 
			 */
			File file = new File(filepath);
			
			if(!file.exists()) {
				throw new FileNotFoundException("Could not find the file " + filepath);
			}
			return new Scanner(new BufferedReader(new FileReader(file), 100*1024));
		}
		
		public String getFileNameFromPath(String filePath) {
			/*
			 * Will extract and return the fileName from a path. 
			 * For easier access in the file_content table.
			 */
			String[] filePathSplit = filePath.split("\\\\");
			return filePathSplit[filePathSplit.length-1];
		}
	
	/*
	 *  File_content related methods 
	 */
		public void insertIntoFileContent(List<File_Content> file_content) {
			/*
			 * Will insert an Array of file_content objects into the cassandra database.
			 */
			file_content.parallelStream().forEach(obj -> 
			repository.save(obj));
		}
		
		public File_Content getLineOfFile(String filename, int line_num) {
			/*
			 * Will fetch a particular line of a particular file.
			 */
			return repository.findByfile_nameAndLine_Num(filename, line_num);
		}
		
	/*
	 * File_tracking related methods
	 */
	
		public int insertIntoFileTrack(File_Tracking file_tracking) {
			/*
			 * Will insert a new entry, to be tracked throughout the file upload process.
			 */
			track_repository.save(file_tracking);
			return file_tracking.getId();
		}
	
		public File_Tracking fileTrackFindByFileName(String filename) {
			/*
			 * To access tracking details using fileName, incase needed.
			 */
			return track_repository.findByFilename(filename);
		}
	
		public void updateFileTrackTable(int id, int count, FileTrackStatus status) {
			/*
			 * Updating the tracking table of a particular id.
			 */
			File_Tracking entry = track_repository.findById(id);
			entry.setCheckpointLine(count);
		    entry.setStatus(status);
		    track_repository.save(entry);
		}
	
	
		public File_Tracking getFileStatusByID(int id) {
			/*
			 * Will return the file_tracking object (a record) corresponding to a particular ID.
			 */
			return track_repository.findById(id);
		}
	
	
		public File_Tracking getfileTrackingStatus(String filename) {
			/*
			 * If ID is already present in our table, we will just return that record.
			 * 
			 * Otherwise, we will create a new entry, initialize the checkpoint to 0 and the status to
			 * "Not done yet" and then, return the object to the used.
			 */
			if(track_repository.findByFilename(filename)==null) {
				File_Tracking entry = new File_Tracking(filename,0,FileTrackStatus.PENDING);
				insertIntoFileTrack(entry);
				return entry;		
			}
			else {
				File_Tracking actualEntity = track_repository.findByFilename(filename);
				return actualEntity;
			}
		}

	
	
	public void parseFile(Scanner sc, int id, FileTrackStatus status, int continue_from)  {
		/*
		 * This function parses through the file in chunks of 10Mb. 
		 * Stores the sentences in file_content table of cassandra and also
		 * updates the file_tracking table.
		 */
		
		List<File_Content> file_content = new ArrayList<>(10000);
		int count = 0;
		
			// A delimiter which will serve as identifying parts of a paragraph into lines.
				sc.useDelimiter("\\.");

					while(sc.hasNext()) {
						
						// Scan and increment as long as the count reaches the checkpoint value.
					    String line = sc.next();
					    count += 1;
					    
					    // Continue from the checkpoint. 
						if(count > continue_from) {
							
							// Storing the above arraylist comprising of file_name, line_count and the line into the table.
							file_content.add(new File_Content(file_name,count,line)); 	
							
						    if(count % batch_size==0) {
						 
						    	// Storing in batches of size 10000 (sentences).   	
						    	insertIntoFileContent(file_content);

						    	// Also update the file_tracking column reflecting the latest checkpoint and the status. 
						    	updateFileTrackTable(id, count, FileTrackStatus.PENDING);
							  
							    // clear the content to avoid out of memory error.
							    file_content.clear(); 
							    continue_from = count;					    
						    }
					    }
					}
					
					/*
					 * To store remaining content or if the total number of lines was lesser than 10000:
					 */
					if(file_content.size()>0) {
						insertIntoFileContent(file_content);
					}
					// Final status to Done.
					updateFileTrackTable(id, count, FileTrackStatus.COMPLETED);
				
				sc.close();
			
	}
	
	public List<File_Content> retrieveContent(int start, int stop) {
		/*
		 * Will retrieve a set of lines based on the start and stop parameters provided by the user.
		 */
		if((stop-start)>retrieve_size) {
			throw new RangeOutOfBounds("The range is greater than 50");
		} else {
			List<File_Content> file_content = new ArrayList<>(retrieve_size);
			for(int i=start;i<=stop;i++) {
				file_content.add(getLineOfFile(file_name, i));
			}
			return file_content;
		}
	}
}
