package com.example.file.parser.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.file.parser.controller.FileContentController;
import com.example.file.parser.exception.RangeOutOfBoundsException;
import com.example.file.parser.model.FileContent;
import com.example.file.parser.repository.FileContentRepository;
import com.example.file.parser.utilities.FileTrackStatusEnum;
import com.example.file.parser.utilities.UtilityFunctions;


/**
 * Service Layer for {@link FileContentController} resource
 */
@Service
public class FileContentService {
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingService trackService;
	
	// batchSize property
	@Value("${batchSize}")
	int batchSize;
	
	Logger logger = LoggerFactory.getLogger(FileContentService.class);

		/**
		 * This function inserts an array of FileContent objects into the 
		 * 	fileContent table.
		 * @param fileContent array
		 * @return fileName
		 */
		public String insertIntoFileContent(List<FileContent> fileContent) {
			
			logger.info("inserting records into FileContent table of cassandra");
			fileContent.parallelStream().forEach(obj -> 
			repository.save(obj));
			return fileContent.get(0).getFileName();
		}
	
	/**
	 * Function to parse a file and store its content in cassandra database.
	 * 	Also updates the filetracking table.	
	 * @param filepath
	 * @param getFileLines
	 * @param id
	 * @param continue_from
	 */
	public void parseFile(String filepath, boolean getFileLines, int id, int continue_from, Scanner sc)  {
		
		logger.info("Inside parseFile function");
		int totalLines = trackService.getTotalLines(id, getFileLines);
		String fileName = UtilityFunctions.getFileNameFromPath(filepath);
		List<FileContent> fileContent = new ArrayList<>(batchSize);
		
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
							
							
							FileContent fileContentInstance = new FileContent();
							fileContentInstance.setFileName(UtilityFunctions.getFileNameFromPath(fileName));
							fileContentInstance.setLineNum(count);
							fileContentInstance.setLine(line);
							
							fileContent.add(fileContentInstance);
							
						    if(count % batchSize==0) {
						    	// Storing in batches of size 10000 (sentences).   	
						    	insertIntoFileContent(fileContent);
						    	logger.info("uploaded "+batchSize+" no. of records to table with partition key "+fileName);

						    	/*
						    	 *  Also update the file_tracking column reflecting the latest 
						    	 *  checkpoint and the status still being, "pending". 
						    	 */
						    	trackService.updateFileTrackTable(id, count,UtilityFunctions.calculateRemaining(totalLines, count),
						    			FileTrackStatusEnum.PENDING);
						    	
						    	logger.info("fileTracking table updated");
							    // clear the content to avoid out of memory error.
						    	fileContent.clear(); 
							    continue_from = count;					    
						    }
					    }
					}
					
					/*
					 * To store remaining content or if the total number of lines was lesser than 10000:
					 */
					if(fileContent.size()>0) {
						insertIntoFileContent(fileContent);
						logger.info("inserted remaining content of arraylist");
					}
					// Final status of that particular file's tracking status to *COMPLETED*.
					trackService.updateFileTrackTable(id, count, UtilityFunctions.calculateRemaining(totalLines, count)
							, FileTrackStatusEnum.COMPLETED);
				logger.info("file upload complete");
				sc.close();
			
	}
	
	/**
	 * This function returns the content (lines) falling between a range.
	 * @param fileName
	 * @param start
	 * @param stop
	 * @param retrieveSize
	 * @return FileContent array.
	 */
	public FileContent[] retrieveContent(String fileName, int start, int stop, int retrieveSize) {
		
		/*
		 * This array will hold the FileContent objects lying between a given start and stop.
		 */
		logger.info("inside retrieve method, params provided are "
				+ "file Name "+ fileName + " startline " + start + " stopline " + stop);
		FileContent[] file_content = null;
		
		/*
		 * If the range is greater than a preset configured retrieveSize, we will throw an exception.
		 */
		if((stop-start)>retrieveSize) {
			logger.warn("Range provided is more than what is allowed :"+ retrieveSize);
			throw new RangeOutOfBoundsException("The range is greater than " + retrieveSize);
		} else {
			// The content (lines) lying between the required parameters, will be returned.
			file_content = repository.findByFileNameAndLineNumBetween(fileName,start,stop);
			}
			return file_content;
		}
	
	}

