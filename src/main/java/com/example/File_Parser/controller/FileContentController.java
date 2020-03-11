package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private FileContentService service;
	@Autowired
	private FileTrackingService trackService;

	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@PreDestroy
	public void shutdown() {
		// To avoid memory leakage.
		executor.shutdown();
	}
	
	@PostMapping("/tasks/parse")
	@ResponseBody
	public FileTracking saveFileContent(@RequestBody FilePath filepath )  {	
			/*
			 * 1. Will check if the file actually exists in the filepath provided, 
			 * 	if yes, a scanner object is returned.
			 * 2. Then, we will obtain a file_tracking object.
			 * 3. Also initialize important variables in the services class.
			 * 4. Next, from the obtained file_tracking object we will check if the status is
			 * 	"COMPLETED". We will proceed only if the status is NOT Completed.
			 * 5. Else throw an exception.
			 */
			
			
			Scanner file_scanner = service.scanFile(filepath.getFilepath());
			
			FileTracking entry = trackService.getfileTrackingStatus(filepath.getFilepath());
			int id = entry.getId();
			FileTrackStatus status = entry.getStatus();
			String fileName = service.getFileNameFromPath(entry.getFilename());
			int continue_from = entry.getCheckpointLine();
					
			// A new thread will take care of the file store process. 
			if(FileTrackStatus.COMPLETED != status) {
			executor.submit(()->{
				service.parseFile(fileName, file_scanner, id, status, continue_from); 
				});
			return entry;
			} 
			else {
				throw new RecordAlreadyExistsException(fileName, id);
			}
	 
	}
	
	@GetMapping
	public FileContent[] getFileContent(@RequestParam Map<String,String> details){
		/*
		 * To retrieve contents of a file within a certain range.
		 */
		int start = Integer.parseInt(details.get("start"));
		int stop = Integer.parseInt(details.get("stop"));
		
		return service.retrieveContent(start, stop);
	}
}
