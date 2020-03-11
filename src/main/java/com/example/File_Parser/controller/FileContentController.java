package com.example.File_Parser.controller;

import java.io.FileNotFoundException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.exception.RecordAlreadyExistsException;
import com.example.File_Parser.model.FilePath;
import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.File_Content;
import com.example.File_Parser.model.File_Tracking;


@RestController
@Async
public class FileContentController {
	@Autowired
	private FileContentService service;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@PreDestroy
	public void shutdown() {
		// To avoid memory leakage.
		System.out.println("yes");
		executor.shutdown();
	}
	
	@PostMapping("/store-in-db")
	@ResponseBody
	public File_Tracking saveFileContent(@RequestBody FilePath filepath ) throws FileNotFoundException {	
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
			
			File_Tracking entry = service.getfileTrackingStatus(filepath.getFilepath());
			int id = entry.getId();
			FileTrackStatus status = entry.getStatus();
			String fileName = service.getFileNameFromPath(entry.getFilename());
			int continue_from = entry.getCheckpointLine();
			
			service.initializeVars(fileName, filepath.getBatch_size(), filepath.getRetrieve_size());
					
			// A new thread will take care of the file store process. 
			if(!("COMPLETED".equalsIgnoreCase(status.name()))) {
			executor.submit(()->{
				service.parseFile(file_scanner, id, status, continue_from); 
				});
			return entry;
			} 
			else {
				throw new RecordAlreadyExistsException(fileName, id);
			}
	 
	}
	
	@GetMapping("/getStatus/{id}")
	public File_Tracking getTrackStatus(@PathVariable("id") int id) {
		// To track the status of file upload.
		return service.getFileStatusByID(id);
	}
	
	@GetMapping("/getContent")
	public List<File_Content> getFileContent(@RequestParam Map<String,String> details){
		/*
		 * To retrieve contents of a file within a certain range.
		 */
		int start = Integer.parseInt(details.get("start"));
		int stop = Integer.parseInt(details.get("stop"));
		
		return service.retrieveContent(start, stop);
	}
}
