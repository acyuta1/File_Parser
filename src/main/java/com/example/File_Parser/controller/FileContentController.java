package com.example.File_Parser.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.model.FilePath;
import com.example.File_Parser.model.File_Content;
import com.example.File_Parser.model.File_Tracking;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;



@RestController
@Async
public class FileContentController {
	@Autowired
	private FileContentService service;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@PreDestroy
	public void shutdown() {
		executor.shutdown();
	}
	
	@PostMapping("/store-in-db")
	@ResponseBody
	public File_Tracking saveFileContent(@RequestBody FilePath filepath ) throws IOException {
					
			File_Tracking entry = service.getfileTrackingStatus(filepath.getFilepath());
			int id = entry.getId();
			String status = entry.getStatus();
			int continue_from = entry.getCheckpointLine();
			
			executor.submit(()->{
			service.ParseFile(filepath.getFilepath(), id, status, continue_from);
			});
		
		return service.getfileTrackingStatus(filepath.getFilepath());
		
	}
	
	@GetMapping("/getStatus/{id}")
	public File_Tracking getTrackStatus(@PathVariable("id") int id) {
		return service.getFileStatusByID(id);
	}
	

}
