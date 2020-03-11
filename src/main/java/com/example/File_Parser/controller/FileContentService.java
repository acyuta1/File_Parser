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
	/**
	 * Service Layer of the FileContentController.
	 */
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingService trackService;
	
	// getting hold of the Configured BatchSize and the RetrieveSize.
	@Value("${batchSize}")
	int batchSize;
	@Value("${retrieveSize}")
	int retrieveSize;


		public Scanner scanFile (String filepath) {
			/**
			 * Returns a new Scanner object from the filepath provided. 
			 */
			File file = new File(filepath);
			Scanner sc = null;
			
			/*
			 * Checks if a file actually exists.
			 * If no, a custom exception exception with HTTP response 400 is thrown.
			 * 
			 */
			if(!(file.exists())) {
				throw new FileDoesNotExistException(filepath);
			} else {
				try {
					/*
					 *  Scanner object which reads a given file in 10MB chunks.
					 *  This is to avoid out of memory exceptions.
					 */
					sc = new Scanner(new BufferedReader(new FileReader(file), 100*1024));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// The scanner object is returned. 
			return sc;
		}
		
		public String getFileNameFromPath(String filePath) {
			/**
			 * Will extract and return the fileName from a path. 
			 * For easier access in the file_content table.
			 */
			String[] filePathSplit = filePath.split("\\\\");
			
			/*
			 *  We are only concerned about the last part of a path,
			 *  which actually is the filename with its extension.
			 *  ex: file.txt
			 */
			return filePathSplit[filePathSplit.length-1];
		}
	
		public void insertIntoFileContent(List<FileContent> file_content) {
			/**
			 * Records to be inserted into the database (Cassandra) are streamed in parallel,
			 * making use of the available cores and then inserted into the database.
			 */
			file_content.parallelStream().forEach(obj -> 
			repository.save(obj));
		}
		
	public void parseFile(String file_name, Scanner sc, int id, FileTrackStatus status, int continue_from)  {
		/**
		 * This function scans through the file, the delimiter being ".", 
		 * Stores the sentences in file_content table of cassandra and also
		 * updates the file_tracking table.
		 */
		
		// An arraylist, which will store the records until they are persisted into the database.
		List<FileContent> file_content = new ArrayList<>(batchSize);
		
		// Our table has a column, lineNum. Count will be used to represent these line numbers.
		int count = 0;
		
			// A delimiter which will serve as identifying parts of a paragraph into lines.
				sc.useDelimiter("\\.");

					/*
					 * Normally, scanner object's next() returns the next *character*, but in our case,
					 * since the delimiter is ".", it will return a sentence!
					 */
					while(sc.hasNext()) {
						
						/*
						 *  Below two lines will scan through the file until the count equals the
						 *  checkpoint value. After which, the addition to above arraylist will begin.
						 */
					    String line = sc.next();
					    count += 1;
					    
					    // Continue from the checkpoint. 
						if(count > continue_from) {
							
							// Storing the above arraylist comprising of file_name, line_count and the line into the table.
							file_content.add(new FileContent(file_name,count,line)); 	
							System.out.println(count);
							
						    if(count % batchSize==0) {
						    	// Storing in batches of size 10000 (sentences).   	
						    	insertIntoFileContent(file_content);

						    	/*
						    	 *  Also update the file_tracking column reflecting the latest 
						    	 *  checkpoint and the status still being, "pending". 
						    	 */
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
						insertIntoFileContent(file_content);
					}
					// Final status of that particular file's tracking status to *COMPLETED*.
					trackService.updateFileTrackTable(id, count, FileTrackStatus.COMPLETED);
				
				sc.close();
			
	}
	
	public FileContent[] retrieveContent(int start, int stop) {
		/**
		 * Will retrieve a set of lines based on the start and stop parameters provided by the user.
		 */
		
		/*
		 * This array will hold the FileContent objects lying between a given start and stop.
		 */
		FileContent[] file_content = null;
		
		/*
		 * If the range is greater than a preset configured retrieveSize, we will throw an exception.
		 */
		if((stop-start)>retrieveSize) {
			throw new RangeOutOfBoundsException("The range is greater than " + retrieveSize);
		} else {
			// The content (lines) lying between the required parameters, will be returned.
			file_content = repository.findByFileNameAndLineNumBetween("newfile_short.txt",start,stop);
			}
			return file_content;
		}
	}

