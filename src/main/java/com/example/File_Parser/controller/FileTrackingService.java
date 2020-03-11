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
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingRepository track_repository;
	@Value("${batchSize}")
	int batchSize;
	@Value("${retrieveSize}")
	int retrieveSize;

	public int insertIntoFileTrack(FileTracking file_tracking) {
		/*
		 * Will insert a new entry, to be tracked throughout the file upload process.
		 */
		track_repository.save(file_tracking);
		return file_tracking.getId();
	}
	
	public FileTracking getfileTrackingStatus(String filename) {
		/*
		 * If ID is already present in our table, we will just return that record.
		 * 
		 * Otherwise, we will create a new entry, initialize the checkpoint to 0 and the status to
		 * "Not done yet" and then, return the object to the used.
		 */
		if(track_repository.findByFilename(filename)==null) {
			FileTracking entry = new FileTracking(filename,0,FileTrackStatus.PENDING);
			insertIntoFileTrack(entry);
			return entry;		
		}
		else {
			FileTracking actualEntity = track_repository.findByFilename(filename);
			return actualEntity;
		}
	}
	
	public void updateFileTrackTable(int id, int count, FileTrackStatus status) {
		/*
		 * Updating the tracking table of a particular id.
		 */
		FileTracking entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
	    entry.setStatus(status);
	    track_repository.save(entry);
	}

	

	public FileTracking fileTrackFindByFileName(String filename) {
		/*
		 * To access tracking details using fileName, incase needed.
		 */
		return track_repository.findByFilename(filename);
	}

	


	public FileTracking getFileStatusByID(int id) {
		/*
		 * Will return the file_tracking object (a record) corresponding to a particular ID.
		 */
		return track_repository.findById(id);
	}

}
