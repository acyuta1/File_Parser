package com.example.file.parser.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.file.parser.repository.FileContentRepository;
import com.example.file.parser.services.FileContentService;

public class UtilityFunctionTests {

	@InjectMocks
	UtilityFunctions utilityFunctions;
	
	
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
	public void when_file_path_is_given_should_return_scanner_object() {
		File file = new File(getClass().getResource("../resources/testlinecontent").getFile());
		Scanner response = UtilityFunctions.scanFile(file.toPath().toString());
		String expectedWord = "this is line1";
		response.useDelimiter("\\.");
		assertEquals(expectedWord, response.next());
		response.close();
	}
	
	
}
