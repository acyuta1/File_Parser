package com.example.file.parser.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.file.parser.exception.NoSuchFileException;
import com.example.file.parser.exception.FileFormatNotCompatibleException;
import com.example.file.parser.exception.RecordAlreadyExistsException;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.services.FileContentService;
import com.example.file.parser.services.FileTrackingService;

public class UtilityFunctions {
	static Logger logger = LoggerFactory.getLogger(UtilityFunctions.class);

	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static Filetrack startParsing(Filetrack filetrack, Scanner sc, FileContentService service, FileTrackingService trackService) {
		logger.info("File to be parsed: " + filetrack.getFilename() + " and status: " + filetrack.getStatus());
		
		if(FileTrackStatusEnum.COMPLETED == filetrack.getStatus()) {
			logger.warn("Duplicate record insertion tried, looking for modification time.");
			System.out.println(getModificationTime(filetrack.getFilename()));
			if(getModificationTime(filetrack.getFilename()).equals(filetrack.getModificationTime())) {
				logger.warn("File is unchanged");
				throw new RecordAlreadyExistsException(filetrack.getFilename(), filetrack.getId());
			}
			else {
				logger.info("New modification time, altering contents of Database to reflect the same.");
			trackService.setModificationTime(filetrack.getId(), getModificationTime(filetrack.getFilename()));
			
			executor.submit(()->{	
				service.parseFile(filetrack.getFilename(), true, filetrack.getId(), filetrack.getCheckpointLine(), sc); 
				});
			return filetrack;
			}
		} else if(FileTrackStatusEnum.NOT_STARTED_YET == filetrack.getStatus()){
			logger.info("upload process will begin now.");
			
				executor.submit(()->{	
					service.parseFile(filetrack.getFilename(), true, filetrack.getId(), 0, sc); 
					});
			return filetrack;
	}
		else if(FileTrackStatusEnum.PENDING == filetrack.getStatus() || FileTrackStatusEnum.FAILED == filetrack.getStatus()){
			logger.info("upload process will continue from last checkpoint.");
			executor.submit(()->{	
				service.parseFile(filetrack.getFilename(), false, filetrack.getId(), filetrack.getCheckpointLine(), sc); 
				});
			return filetrack;
		}
		return filetrack;
	}
	
	public static int getFileDetails(String filepath) {
		int lineCount = 0;
		/*
		 * Checks if a file actually exists.
		 * If no, a custom exception exception with HTTP response 400 is thrown.
		 * 
		 */
		logger.info("Process to fetch Line Count of the file " + filepath + " will begin.");
			try {
				/*
				 *  Scanner object which reads a given file in 10MB chunks.
				 *  This is to avoid out of memory exceptions.
				 */
				FileInputStream stream = new FileInputStream(filepath);
				byte[] buffer = new byte[8192];
				int n;
				while((n= stream.read(buffer))>0) {
					for(int i =0; i<n;i++) {
						if(buffer[i]=='.') {
							lineCount++;
						}
					}
				}
			
			stream.close();
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		// The scanner object is returned. 
		logger.info("Line count of file " + filepath + " is " + lineCount);
		return lineCount;
	}
	
	/**
	 * Method to return a scanner object.
	 * @param filepath
	 * @return Scanner object for the file provided. 
	 */
	public static Scanner scanFile (String filepath) {
		
		File file = new File(filepath);
		Scanner sc = null;
		if(!(getFileNameFromPath(filepath).contains(".txt"))) {
			logger.warn("Incorrect file type provided. Cannot be parsed!");
			throw new FileFormatNotCompatibleException(filepath);
		}
		if(!(file.exists())) {
			logger.warn("File in the specified directory could not be found :" + filepath);
			throw new NoSuchFileException(filepath);
		}
			else {
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
	
	/**
	 * This function returns the name from an entire full path. 
	 * @param filePath
	 * @return fileName
	 */
	public static String getFileNameFromPath(String filePath) {
		
		String[] filePathSplit = filePath.split("\\\\");
		
		/*
		 *  We are only concerned about the last part of a path,
		 *  which actually is the filename with its extension.
		 *  ex: file.txt
		 */
		logger.info("filename with extension is obtained.");
		return filePathSplit[filePathSplit.length-1];
	}
	
	/**
	 * This function calculates the percentage of file upload done. 
	 * @param total
	 * @param batchDone
	 * @return percentage completed.
	 */
	public static float calculateRemaining (int total, int batchDone) {
		logger.info("Calculating the total percentage of upload completed.");
		return ((float)batchDone/total)*100;
	}
	
	public static Long getModificationTime(String filepath) {
		logger.info("Fetching last modified time of the file provided.");
		File file = new File(filepath);
		return file.lastModified();
	}
}

