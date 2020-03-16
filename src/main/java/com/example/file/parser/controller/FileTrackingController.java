package com.example.file.parser.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.file.parser.model.Filetrack;
import com.example.file.parser.services.FileTrackingService;

/**
 * Controller class for {@link FileTracking} resource
 * @author Achyutha.aluru
 *
 */
@RestController
@Async
@RequestMapping("/fileTrack")
public class FileTrackingController {
	
	@Autowired
	private FileTrackingService trackService;
	
	Logger logger = LoggerFactory.getLogger(FileTrackingController.class);
	
	/**
	 * This function is used to obtain the tracking status of a file upload.
	 * @param id
	 * @return FileTracking object matching the ID provided.
	 */
	@GetMapping("/getStatus/{id}")
	public Filetrack getTrackStatus(@PathVariable("id") int id) {
		logger.info("tracking status of file with id: "+id);
		return trackService.getFileStatusByID(id);
	}

}
