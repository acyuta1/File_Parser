package com.example.file.parser.controller;

import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.file.parser.exception.RecordAlreadyExistsException;
import com.example.file.parser.model.FileContent;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.services.FileContentService;
import com.example.file.parser.services.FileTrackingService;
import com.example.file.parser.utilities.Constants;
import com.example.file.parser.utilities.UtilityFunctions;

/**
 * Controller class for {@link FileContent} resource
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
	
	@Value("${retrieveSize}")
	int retrieveSize;
	
	Logger logger = LoggerFactory.getLogger(FileContentController.class);
	
	/**
	 * This function parses a given file and stores the content
	 *  in cassandra database.
	 * @param filepath 
	 * @return A filetracking object to track status if the status of that object is PENDING,
	 * 		   Otherwise, a {@link RecordAlreadyExistsException} is thrown.
	 */
	@PostMapping("/tasks/parse")
	@ResponseBody
	public Filetrack saveFileContent(@RequestBody FileContent filepath )  {	
			
		logger.info("inside saveFileContent of FileContentController");
			
			// A tracking record corresponding to the file provided is obtained.
			Scanner sc = UtilityFunctions.scanFile(filepath.getFileName());
			Filetrack entry = trackService.getfileTrackingStatus(filepath.getFileName());
					
			/*
			 * IF the status is COMPLETED, an exception is thrown.
			 * Else, IF the status is NOT_STARTED_YET, the getFileLines is set to 
			 * 		true and the parse process starts.
			 * ELse, the getFileLines is set to false as the process was already 
			 * 		started and the upload continues from last checkpoint.
			 */
			
			UtilityFunctions.startParsing(entry, sc, service, trackService);
			return entry;
	 
	}
	
	/**
	 * Method to obtain lines between a range (start and stop).
	 * @param details - Map object with params start and stop. 
	 * @return FileContent[] falling between the two params.
	 */
	@GetMapping
	public FileContent[] getFileContent(@RequestParam Map<String,String> requestParams){
		/*
		 * Parameters required for this method are:
		 * 	i. FileName
		 * 	ii. A start line
		 * 	iii. A stop line.
		 * All of the content BETWEEN these two parameters will be returned.
		 */
		logger.info("get request with parameters fileName: " + Constants.FILE_NAME + " start-line " + Constants.START_LINE
				+ " stop-line " + Constants.STOP_LINE );
		logger.info("retrieve size is set to " + retrieveSize);
		String fileName = requestParams.get(Constants.FILE_NAME);
		int start = Integer.parseInt(requestParams.get(Constants.START_LINE));
		int stop = Integer.parseInt(requestParams.get(Constants.STOP_LINE));
		
		return service.retrieveContent(fileName, start, stop, retrieveSize);
	}
}
