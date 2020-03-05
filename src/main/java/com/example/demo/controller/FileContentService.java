package com.example.demo.controller;

import java.util.List;

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


//	public FileContentService() {
//		
//		// TODO Auto-generated constructor stub
//	}
	
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
	
	public void fileTrackingUpdate(int id, int count) {
		File_Tracking entry = track_repository.findById(id);
		entry.setCheckpointLine(count);
	    entry.setStatus("Not done yet");
	    track_repository.save(entry);
	}
	

}
