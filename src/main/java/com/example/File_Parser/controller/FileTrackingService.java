package com.example.File_Parser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.FileTracking;
import com.example.File_Parser.repository.FileContentRepository;
import com.example.File_Parser.repository.FileTrackingRepository;

@Service
public class FileTrackingService {
	/**
	 * Service Layer of the FileTrackingController.
	 */
	
	@Autowired
	private FileTrackingRepository track_repository;
	
	public int insertIntoFileTrack(FileTracking file_tracking) {
		/**
		 * Will insert a new unique entry of a particular
		 * file, to be tracked throughout the file upload process.
		 */
		track_repository.save(file_tracking);
		
		// Returns the ID of the created File tracking record. 
		return file_tracking.getId();
	}
	
	public FileTracking getfileTrackingStatus(String filename) {
		/**
		 * As a request is made with a valid filename, we will either extract that record
		 * if it already exists in the database (FileTracking table) 
		 * <OR>
		 * Create a new entry in our table. 
		 */
		
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
	
	public void updateFileTrackTable(int id, int count, FileTrackStatus status) {
		/**
		 * Updating the tracking table of a particular id.
		 */
		
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

	

	public FileTracking fileTrackFindByFileName(String filename) {
		/**
		 * To access tracking details using fileName, incase needed.
		 */
		return track_repository.findByFilename(filename);
	}

	


	public FileTracking getFileStatusByID(int id) {
		/**
		 * Will return the file_tracking object (a record) corresponding to a particular ID.
		 */
		return track_repository.findById(id);
	}

}
