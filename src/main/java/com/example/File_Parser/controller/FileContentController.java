package com.example.File_Parser.controller;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.exception.RecordAlreadyExistsException;
import com.example.File_Parser.model.FileContent;
import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.FileTracking;


/**
 * Controller class for {@link FileContent} resource
 */
/**
 * @author Achyutha.aluru
 *
 */
@RestController
@Async
@RequestMapping("/fileContent")
public class FileContentController {
	
	@Autowired
	private FileContentService service;
	@Autowired
	private FileTrackingService trackService;

	@Value("${params}")
	String[] params;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	/**
	 * Method to initiate the parsing of a file.
	 * @param filepath.
	 * @return A filetracking object to track status if the status of that object is PENDING,
	 * 		   Otherwise, a {@link RecordAlreadyExistsException} is thrown.
	 */
	@PostMapping("/tasks/parse")
	@ResponseBody
	public FileTracking saveFileContent(@RequestBody FileContent filepath )  {	
		
			/*
			 * First step would be to check if the file actually exists.
			 */
			Scanner file_scanner = service.scanFile(filepath.getFileName());
			
			/*
			 * Next step would be to:
			 * 	i. Either retrieve an object which already exists in the tracking table.
			 * 	ii. or, Create a new record and return that object.
			 */
			FileTracking entry = trackService.getfileTrackingStatus(filepath.getFileName());
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
	
	/**
	 * Method to obtain lines between a range (start and stop).
	 * @param details - Map object with params start and stop. 
	 * @return FileContent[] falling between the two params.
	 */
	@GetMapping
	public FileContent[] getFileContent(@RequestParam Map<String,String> details){
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
