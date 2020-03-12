package com.example.file.parser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.file.parser.model.FileTracking;
import com.example.file.parser.services.FileTrackingService;

/**
 * Controller class for {@link FileTracking} resource
 */
/**
 * @author Achyutha.aluru
 *
 */
@RestController
@Async
@RequestMapping("/fileTrack")
public class FileTrackingController {
	
	@Autowired
	private FileTrackingService trackService;
	
	/**
	 * Method to track the status of file upload.
	 * @param id
	 * @return FileTracking object matching the ID provided.
	 */
	@GetMapping("/getStatus/{id}")
	public FileTracking getTrackStatus(@PathVariable("id") int id) {
		return trackService.getFileStatusByID(id);
	}

}
