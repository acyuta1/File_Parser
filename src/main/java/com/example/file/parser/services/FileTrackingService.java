package com.example.file.parser.services;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.file.parser.controller.FileTrackingController;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.repository.FileTrackingRepository;
import com.example.file.parser.utilities.FileTrackStatusEnum;
import com.example.file.parser.utilities.UtilityFunctions;


/**
 * Service Layer for {@link FileTrackingController} resource
 */
@Service
public class FileTrackingService {
	
	@Autowired
	private FileTrackingRepository track_repository;
	
	Logger logger = LoggerFactory.getLogger(FileTrackingService.class);

	
	/**
	 * Method to insert into file tracking table.
	 * @param file_tracking
	 * @return The ID of the filetracking object.
	 */
	public int insertIntoFileTrack(Filetrack file_tracking) {
		logger.info("inserting into fileTracking table the file "+file_tracking.getFilename());
		track_repository.save(file_tracking); 
		// Returns the ID of the created filetracking record.
		return file_tracking.getId();
	}
	
	/**
	 * Method to get the status of file tracking upload.
	 * @param fileName
	 * @return FileTracking object
	 */
	public Filetrack getfileTrackingStatus(String filename) {
	
		/*
		 * If there is no entry present matching the filename, 
		 * we will create a new entry with the initial parameters: 
		 * filename, 0 (checkpoint line), pending (status).
		 */
	
		if(track_repository.findByFilename(filename)==null) {
			Filetrack entry = new Filetrack();
			entry.setFilename(filename);
			entry.setCheckpointLine(0);
			entry.setTotalLinesPresent(0);
			entry.setPercentComplete(0);
			entry.setStatus(FileTrackStatusEnum.NOT_STARTED_YET);
			insertIntoFileTrack(entry);
			logger.info("new filetrack status with name "+filename+" created");
			return entry;		
		}
		/*
		 * If there is a record with a matching fileName, 
		 * we will just return that particular record as a FileTracking object.
		 */
		else {
			Filetrack actualEntity = track_repository.findByFilename(filename);
			logger.info("Filetrack record with name "+filename+" retrieved.");
			return actualEntity;
		}
	} //done
	
	/**
	 * Method to update the fileTracking status of a file.
	 * @param id
	 * @param count
	 * @param status
	 */
	public void updateFileTrackTable(int id, int count, float percentDone, FileTrackStatusEnum status) {
		logger.info("updating file tracking table with id ");
		Filetrack entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
		entry.setPercentComplete(percentDone);
	    entry.setStatus(status);
	    track_repository.save(entry);
	}
	
	/**
	 * Method to get the total line count of a file.
	 * @param id
	 * @param getFileLines
	 * @return totalLines, the line count
	 */
	public int getTotalLines(int id, boolean getFileLines) {
		int totalLines = 0;
		/*
		 * If getFileLines is true, we will obtain the line count and update
		 * the particular record.
		 * Else, the linecount will be retrieved from the record and returned.
		 */
		if(getFileLines) {
			logger.info("line read for file with id "+id+" started");
			Filetrack entry = track_repository.findById(id);
			totalLines = UtilityFunctions.getFileDetails(entry.getFilename());
			entry.setTotalLinesPresent(totalLines);
			entry.setStatus(FileTrackStatusEnum.PENDING);
		    track_repository.save(entry);
		    logger.info("line read finished with total lines "+totalLines);
		    return totalLines;
		} else {
			totalLines = getFileStatusByID(id).getTotalLinesPresent();
			return totalLines;
		}
		
	}

	/**
	 * Method to findTheTracking details by filename.
	 * @param filename
	 * @return FileTracking object.
	 */
	public Filetrack fileTrackFindByFileName(String filename) {
		return track_repository.findByFilename(filename);
	}

	/**
	 * Method to get the status of a file upload.
	 * @param id
	 * @return FileTracking object.
	 */
	public Filetrack getFileStatusByID(int id) {
		return track_repository.findById(id);
	}

}
