package com.example.File_Parser.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.exception.RecordAlreadyExistsException;
import com.example.File_Parser.model.FilePath;

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
		executor.shutdown();
	}
	
	@PostMapping("/store-in-db")
	@ResponseBody
	public File_Tracking saveFileContent(@RequestBody FilePath filepath ) throws IOException {	
			/*
			 * Tracking ID of the file-name provided will be obtained, which will be 
			 * immediately returned to the user.
			 * After which, a new thread will start the process of inserting into the database.
			 * The tracking ID which was returned can then be used to track the status of the 
			 * file upload.
			 */
			File_Tracking entry = service.getfileTrackingStatus(filepath.getFilepath());
			int id = entry.getId();
			String status = entry.getStatus();
			String fileName = service.getFileNameFromPath(entry.getFilename());
			int continue_from = entry.getCheckpointLine();
			System.out.println(status);
					
			// A new thread will take care of the file store process. 
			if(!(status.equals("done"))) {
			executor.submit(()->{
				
						try {
							service.parseFile(filepath.getFilepath(), id, status, continue_from);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
	

}
