package com.example.file.parser.utilities;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.file.parser.exception.FileFormatNotCompatibleException;
import com.example.file.parser.exception.RangeOutOfBoundsException;
import com.example.file.parser.exception.RecordAlreadyExistsException;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.repository.FileContentRepository;
import com.example.file.parser.repository.FileTrackingRepository;
import com.example.file.parser.services.FileContentService;
import com.example.file.parser.services.FileTrackingService;

public class UtilityFunctionTests {

	
	@InjectMocks
	UtilityFunctions utilityFunctions;
	@Mock
	UtilityFunctions utilityFunctions1;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void when_total_and_batch_provided_should_return_percentage() {
		assertEquals(utilityFunctions.calculateRemaining(100, 100),(100.0f));
	}
	
	@Test
	public void when_file_path_is_given_should_return_file_name() {
		String filePath = "C:\\Users\\achyutha.aluru\\Desktop\\Files\\small_file.txt";
		String expectedOutput = "small_file.txt";
		String responseOutput = UtilityFunctions.getFileNameFromPath(filePath);
		assertEquals(expectedOutput,responseOutput);
	}
	
	@Test
	public void when_file_is_given_should_return_its_total_line_count() throws IOException {
		File file = new File(getClass().getResource("../resources/testlinecontent").getFile());
		int responseLineCount = UtilityFunctions.getFileDetails(file.toPath().toString());
		int expectedLineCount = 3;
		assertEquals(expectedLineCount,responseLineCount);
	}
	
	@Test 
	public void when_file_path_with_wrong_extension_is_given_should_throw_exception() {
		File file = new File(getClass().getResource("../resources/testlinecontent").getFile());
//		Scanner response = ;
//		String expectedWord = "this is line1";
//		response.useDelimiter("\\.");
//		assertEquals(expectedWord, response.next());
//		response.close();
		
		assertThatThrownBy(()-> UtilityFunctions.scanFile(file.toPath().toString()))
		.isInstanceOf(FileFormatNotCompatibleException.class);
	}
	
	@Test 
	public void when_file_path_is_given_should_return_scanner_object() {
		File file = new File(getClass().getResource("../resources/testlinecontent.txt").getFile());
		Scanner response = UtilityFunctions.scanFile(file.toPath().toString());
		String expectedWord = "this is line1";
		response.useDelimiter("\\.");
		assertEquals(expectedWord, response.next());
		response.close();
	}
	
	@Test
	public void when_same_file_with_same_modification_time_is_given_should_throw_error() {
		Filetrack fileTrack = new Filetrack();
		FileTrackingRepository repository;
		FileTrackingService trackService = null;
		FileContentService service = null;
		fileTrack.setId(1);
		fileTrack.setFilename("test");
		fileTrack.setCheckpointLine(10000);
		fileTrack.setPercentComplete(10.0f);
		fileTrack.setTotalLinesPresent(100000);
		fileTrack.setStatus(FileTrackStatusEnum.COMPLETED);
		fileTrack.setModificationTime((long)0);
		Scanner sc = null;
		
		assertThatThrownBy(()-> UtilityFunctions.startParsing(fileTrack, sc, service, trackService))
		.isInstanceOf(RecordAlreadyExistsException.class);
	}
	
	@Test
	public void when_filepath_is_given_obtain_modification_time() {
		File file = new File(getClass().getResource("../resources/testlinecontent").getFile());
		
		assertEquals(1584388304573L,(utilityFunctions.getModificationTime(file.toPath().toString())));
	}
	
	
}
