package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.exception.FileDoesNotExistException;
import com.example.File_Parser.exception.RecordAlreadyExistsException;
import com.example.File_Parser.model.FileContent;
import com.example.File_Parser.model.FilePath;
import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.FileTracking;


@RestController
@Async
@RequestMapping("/fileContent")
public class FileContentController {
	/**
	 * Controller class for Model FileContent. 
	 * Endpoints:
	 * 		to Parse a file, 
	 * 		get its content (lines)
	 */
	@Autowired
	private FileContentService service;
	@Autowired
	private FileTrackingService trackService;

	@Value("${params}")
	String[] params;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@PreDestroy
	public void shutdown() {
		// To avoid memory leakage.
		executor.shutdown();
	}
	
	@PostMapping("/tasks/parse")
	@ResponseBody
	public FileTracking saveFileContent(@RequestBody FilePath filepath )  {	
			/**
			 * 1. Will check if the file actually exists in the filepath provided, 
			 * 	if yes, a scanner object is returned.
			 * 2. Then, we will obtain a file_tracking object.
			 * 3. Also initialize important variables in the services class.
			 * 4. Next, from the obtained file_tracking object we will check if the status is
			 * 	"COMPLETED". We will proceed only if the status is NOT Completed.
			 * 5. Else throw an exception.
			 */
			
			/*
			 * First step would be to check if the file actually exists.
			 * If yes, its scanner object is returned.
			 * Else, an exception is thrown.
			 */
			Scanner file_scanner = service.scanFile(filepath.getFilepath());
			
			/*
			 * Next step would be to:
			 * 	i. Either retrieve an object which already exists in the tracking table.
			 * 	ii. or, Create a new record and return that object.
			 */
			FileTracking entry = trackService.getfileTrackingStatus(filepath.getFilepath());
			int id = entry.getId();
			FileTrackStatus status = entry.getStatus();
			String fileName = service.getFileNameFromPath(entry.getFilename());
			int continue_from = entry.getCheckpointLine();
					
			/*
			 * If the status of previously retrieved object is NOT completed,
			 * that would mean it was failed and theres still some part of the file left (to be uploaded).
			 * Hence, a new thread will take care of that process.
			 */
			if(FileTrackStatus.COMPLETED != status) {
			executor.submit(()->{
				service.parseFile(fileName, file_scanner, id, status, continue_from); 
				});
			return entry;
			} 
			
			/*
			 * If the status is COMPLETED, then that would mean a user is requesting for the 
			 * storage of a duplicate file. So, appropriate exception is thrown.
			 */
			else {
				throw new RecordAlreadyExistsException(fileName, id);
			}
	 
	}
	
	@GetMapping
	public FileContent[] getFileContent(@RequestParam Map<String,String> details){
		/**
		 * To retrieve contents of a file within a certain range.
		 */
		
		/*
		 * Parameters required for this method are:
		 * 	i. A start line
		 * 	ii. A stop line.
		 * All of the content BETWEEN these two parameters will be returned.
		 */
		int start = Integer.parseInt(details.get(params[0]));
		int stop = Integer.parseInt(details.get(params[1]));
		
		return service.retrieveContent(start, stop);
	}
}
