package com.example.File_Parser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.File_Parser.model.FileTracking;

@RestController
@Async
@RequestMapping("/fileTrack")
public class FileTrackingController {
	
	@Autowired
	private FileContentService service;
	@Autowired
	private FileTrackingService trackService;
	
	@GetMapping("/getStatus/{id}")
	public FileTracking getTrackStatus(@PathVariable("id") int id) {
		// To track the status of file upload.
		return trackService.getFileStatusByID(id);
	}

}
