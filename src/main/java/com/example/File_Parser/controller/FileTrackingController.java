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
	/**
	 * Controller class for Model FileTracking. 
	 * Endpoints:
	 * 		to get the status of a file upload.
	 */
	@Autowired
	private FileContentService service;
	@Autowired
	private FileTrackingService trackService;
	
	@GetMapping("/getStatus/{id}")
	public FileTracking getTrackStatus(@PathVariable("id") int id) {
		/**
		 * To track the status based on the path variable provided by the user.
		 */
		return trackService.getFileStatusByID(id);
	}

}
