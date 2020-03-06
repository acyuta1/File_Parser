package com.example.demo.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.File_Content;
import com.example.demo.model.File_Tracking;
import com.example.demo.repository.FileContentRepository;
import com.example.demo.repository.FileTrackingRepository;


@Service
public class FileContentService {
	
	@Autowired
	private FileContentRepository repository;
	@Autowired
	private FileTrackingRepository track_repository;
	
	public void fileSentenceInsert(List<File_Content> file_content) {
		repository.saveAll(file_content);
	}
	
	public int fileTrackInsert(File_Tracking file_tracking) {
		track_repository.save(file_tracking);
		return file_tracking.getId();
	}
	
	public File_Tracking fileTrackFindByFileName(String filename) {
		return track_repository.findByFilename(filename);
	}
	
	public void fileTrackingUpdate(int id, int count, String status) {
		/*
		 * Updating the tracking table.
		 */
		File_Tracking entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
	    entry.setStatus(status);
	    track_repository.save(entry);
	}
	
	public File_Tracking fileTrackingInit(String filename) {
		/*
		 * If ID of the file (hash value) is already present in our file_tracking table,
			 * then we would just get hold of the "status" and the "checkpoint" columns of our table.
			 * 
			 * Else, we would make a new entry in the file_tracking table and initialize status to "not done yet"
			 * and checkpoint to 0.
			 * 
			 * Returns the instance.
		 */
		if(track_repository.findByFilename(filename)==null) {
			File_Tracking entry = new File_Tracking(filename,0,"Not done yet");
			fileTrackInsert(entry);
			return entry;
			
		}
		else {
			File_Tracking actualEntity = track_repository.findByFilename(filename);
			return actualEntity;
		}
	}
	

}
