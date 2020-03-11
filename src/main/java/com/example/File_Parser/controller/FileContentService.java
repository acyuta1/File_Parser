package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.File_Parser.exception.FileDoesNotExistException;
import com.example.File_Parser.exception.RangeOutOfBoundsException;
import com.example.File_Parser.model.FileContent;
import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.FileTracking;
import com.example.File_Parser.repository.FileContentRepository;
import com.example.File_Parser.repository.FileTrackingRepository;


@Service
public class FileContentService {
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingRepository track_repository;
	@Autowired
	private FileTrackingService trackService;
	@Value("${batchSize}")
	int batchSize;
	@Value("${retrieveSize}")
	int retrieveSize;

	
	
	/**
	 * Misc methods
	 * @throws FileNotFound1Exception 
	 * @throws FileNotFoundException 
	 * 
	 */
		public Scanner scanFile (String filepath) {
			/*
			 * Returns a new Scanner object from the filepath provided. 
			 */
			File file = new File(filepath);
			Scanner sc = null;
			if(!(file.exists())) {
				throw new FileDoesNotExistException(filepath);
			} else {
				try {
					sc = new Scanner(new BufferedReader(new FileReader(file), 100*1024));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return sc;
			
			
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
		public void insertIntoFileContent(List<FileContent> file_content) {
			/*
			 * Will insert an Array of file_content objects into the cassandra database.
			 */
			file_content.parallelStream().forEach(obj -> 
			repository.save(obj));
		}
		
		
	/*
	 * File_tracking related methods
	 */
		
	
	public void parseFile(String file_name, Scanner sc, int id, FileTrackStatus status, int continue_from)  {
		/*
		 * This function parses through the file in chunks of 10Mb. 
		 * Stores the sentences in file_content table of cassandra and also
		 * updates the file_tracking table.
		 */
		
		List<FileContent> file_content = new ArrayList<>(batchSize);
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
							file_content.add(new FileContent(file_name,count,line)); 	
							System.out.println(count);
						    if(count % batchSize==0) {
						    	System.out.println(count);
						    	// Storing in batches of size 10000 (sentences).   	
						    	insertIntoFileContent(file_content);

						    	// Also update the file_tracking column reflecting the latest checkpoint and the status. 
						    	trackService.updateFileTrackTable(id, count, FileTrackStatus.PENDING);
							  
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
						System.out.println("im here");
						insertIntoFileContent(file_content);
					}
					// Final status to Done.
					trackService.updateFileTrackTable(id, count, FileTrackStatus.COMPLETED);
				
				sc.close();
			
	}
	
	public FileContent[] retrieveContent(int start, int stop) {
		/*
		 * Will retrieve a set of lines based on the start and stop parameters provided by the user.
		 */
		FileContent[] file_content = null;
		if((stop-start)>retrieveSize) {
			throw new RangeOutOfBoundsException("The range is greater than " + retrieveSize);
		} else {
			file_content = repository.findByFileNameAndLineNumBetween("newfile_short.txt",start,stop);
			}
			return file_content;
		}
	}

