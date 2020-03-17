package com.example.file.parser.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.file.parser.model.Filetrack;
import com.example.file.parser.repository.FileTrackingRepository;
import com.example.file.parser.services.FileTrackingService;
import com.example.file.parser.utilities.FileTrackStatusEnum;

public class FileTrackingServiceTests {
	
	@InjectMocks
	FileTrackingService fileTrackingService;
	
	@Mock
	FileTrackingRepository repository;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void when_insert_it_should_return_id() {
		Filetrack fileTrack = new Filetrack();
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(0);
		fileTrack.setPercentComplete(0);
		fileTrack.setTotalLinesPresent(0);
		fileTrack.setStatus(FileTrackStatusEnum.NOT_STARTED_YET);
		int id = fileTrackingService.insertIntoFileTrack(fileTrack);
		assertThat(id).isSameAs(fileTrack.getId());
	}
	
	@Test
	public void when_non_existing_file_is_given_a_new_entry_is_returned() {
		Filetrack entry = fileTrackingService.getfileTrackingStatus("test.txt");		
		
		assertThat(entry.getFilename()).isSameAs("test.txt");
		assertEquals(0.0f, entry.getPercentComplete());
		assertEquals(FileTrackStatusEnum.NOT_STARTED_YET, entry.getStatus());
		assertEquals(0, entry.getCheckpointLine());
	}
	
	@Test
	public void when_existing_file_is_given_the_entry_is_returned() {
		Filetrack fileTrack = new Filetrack();
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(10000);
		fileTrack.setPercentComplete(10.0f);
		fileTrack.setTotalLinesPresent(100000);
		fileTrack.setStatus(FileTrackStatusEnum.PENDING);
		doReturn(fileTrack).when(repository).findByFilename("test");
		
		Filetrack entry = fileTrackingService.getfileTrackingStatus("test");
		assertThat(entry.getFilename()).isSameAs("test");
		assertEquals(10.0f, entry.getPercentComplete());
		assertEquals(FileTrackStatusEnum.PENDING, entry.getStatus());
		assertEquals(10000, entry.getCheckpointLine());
	}
	
	@Test
	public void get_total_lines_if_already_present() {
		Filetrack fileTrack = new Filetrack();
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(10000);
		fileTrack.setPercentComplete(10.0f);
		fileTrack.setTotalLinesPresent(100000);
		fileTrack.setStatus(FileTrackStatusEnum.PENDING);
		doReturn(fileTrack).when(repository).findById(1);
		
		int lineCount = fileTrackingService.getTotalLines(1, false);
		assertEquals(100000, lineCount);
	}
	
	@Test
	public void get_entry_when_filename_is_given() {
		Filetrack fileTrack = new Filetrack();
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(10000);
		fileTrack.setPercentComplete(10.0f);
		fileTrack.setTotalLinesPresent(100000);
		fileTrack.setStatus(FileTrackStatusEnum.PENDING);
		doReturn(fileTrack).when(repository).findByFilename("test");
		
		Filetrack entry = fileTrackingService.fileTrackFindByFileName("test");
		assertThat(entry.getFilename()).isSameAs("test");
		assertEquals(10.0f, entry.getPercentComplete());
		assertEquals(FileTrackStatusEnum.PENDING, entry.getStatus());
		assertEquals(10000, entry.getCheckpointLine());
		
	}
	
	@Test
	public void get_entry_when_id_is_given() {
		Filetrack fileTrack = new Filetrack();
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(10000);
		fileTrack.setPercentComplete(10.0f);
		fileTrack.setTotalLinesPresent(100000);
		fileTrack.setStatus(FileTrackStatusEnum.PENDING);
		doReturn(fileTrack).when(repository).findById(1);
		
		Filetrack entry = fileTrackingService.getFileStatusByID(1);
		assertThat(entry.getFilename()).isSameAs("test");
		assertEquals(10.0f, entry.getPercentComplete());
		assertEquals(FileTrackStatusEnum.PENDING, entry.getStatus());
		assertEquals(10000, entry.getCheckpointLine());
		
	}
	
	
	

	
	

}
