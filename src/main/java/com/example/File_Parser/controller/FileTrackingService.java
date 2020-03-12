package com.example.File_Parser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.FileTracking;
import com.example.File_Parser.repository.FileTrackingRepository;

/**
 * Service Layer for {@link FileTrackingController} resource
 */
@Service
public class FileTrackingService {
	
	@Autowired
	private FileTrackingRepository track_repository;
	
	/**
	 * Method to insert into file tracking table.
	 * @param file_tracking
	 * @return The ID of the filetracking object.
	 */
	public int insertIntoFileTrack(FileTracking file_tracking) {
		
		track_repository.save(file_tracking);
		
		// Returns the ID of the created File tracking record. 
		return file_tracking.getId();
	}
	
	/**
	 * Method to get the status of file tracking upload.
	 * @param fileName
	 * @return FileTracking object
	 */
	public FileTracking getfileTrackingStatus(String filename) {
	
		/*
		 * If there is no entry present matching the filename, 
		 * we will create a new entry with the initial parameters: 
		 * filename, 0 (checkpoint line), pending (status).
		 */
		if(track_repository.findByFilename(filename)==null) {
			FileTracking entry = new FileTracking(filename,0,FileTrackStatus.PENDING);
			insertIntoFileTrack(entry);
			return entry;		
		}
		/*
		 * If there is a record with a matching fileName, 
		 * we will just return that particular record as a FileTracking object.
		 */
		else {
			FileTracking actualEntity = track_repository.findByFilename(filename);
			return actualEntity;
		}
	}
	
	/**
	 * Method to update the fileTracking status of a file.
	 * @param id
	 * @param count
	 * @param status
	 */
	public void updateFileTrackTable(int id, int count, FileTrackStatus status) {
		
		/*
		 * Extract the record with matching ID,
		 * update its checkpoint line to the latest count,
		 * update its status. 
		 */
		FileTracking entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
	    entry.setStatus(status);
	    track_repository.save(entry);
	}

	

	/**
	 * Method to findTheTracking details by filename.
	 * @param filename
	 * @return FileTracking object.
	 */
	public FileTracking fileTrackFindByFileName(String filename) {
		return track_repository.findByFilename(filename);
	}

	/**
	 * Method to get the status of a file upload.
	 * @param id
	 * @return FileTracking object.
	 */
	public FileTracking getFileStatusByID(int id) {
		return track_repository.findById(id);
	}

}
