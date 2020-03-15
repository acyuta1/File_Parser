package com.example.file.parser.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;

import com.example.file.parser.DemoApplicationTests;
import com.example.file.parser.exception.FileDoesNotExistException;
import com.example.file.parser.exception.RangeOutOfBoundsException;
import com.example.file.parser.exception.RecordAlreadyExistsException;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.services.FileContentService;
import com.google.common.base.Optional;


public class FileContentControllerTests extends DemoApplicationTests {
	
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}
	
	
	@Test
	public void saveNewFileContent() throws Exception {
		/*
		 * Tries to save a new file. If successful,
		 * HTTP response 200 is expected.
		 */
		String uri = "/fileContent/tasks/parse";
		File file = new File(getClass().getResource("JSON_Inputs/Input").getFile());
		File expectedOutputFile = new File(getClass().getResource("JSON_Inputs/ExpectedOutput").getFile());
		String content = new String(Files.readAllBytes(file.toPath()));
		String expectedOuput = new String(Files.readAllBytes(expectedOutputFile.toPath()));
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String responseOutput = mvcResult.getResponse().getContentAsString();
//		JSONAssert.assertEquals(expectedOuput, json_content, false);
		assertEquals(expectedOuput, responseOutput);
	}
	
	@Test
	public void saveExistingFileContent() throws Exception {
		/*
		 * Tries to save the same file as above. 
		 * Expected response is 400.
		 */
		String uri = "/fileContent/tasks/parse";
		File file = new File(getClass().getResource("JSON_Inputs/Input").getFile());
		String expectedMessage = "File with fileName test.txt and ID 1 Already exists";
		String content = new String(Files.readAllBytes(file.toPath()));
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andExpect(status().is4xxClientError()).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);	
		String responseMessage = mvcResult.getResolvedException().getMessage();
		assertEquals(expectedMessage, responseMessage);
	}
	
	
	
//	
	@Test
	public void getContent() throws Exception {
		/*
		 * Tries to retrieve a set of lines of a particular file present in cassandra database.
		 * Expected response is 200.
		 */
		String uri = "/fileContent";
		File file = new File(getClass().getResource("JSON_Inputs/toObtainExpectedOutput").getFile());
		String content = new String(Files.readAllBytes(file.toPath()));
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		details.add("filename", "test.txt");
		details.add("start", "1");
		details.add("stop", "3");
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String lineObtained = mvcResult.getResponse().getContentAsString();
        assertEquals(content,lineObtained);
	}
	
	@Test
	public void getContentWhenRangeIsGreater() throws Exception {
		/*
		 * Tries to retrieve a set of lines of a particular file present in cassandra database.
		 * This time, the range provided by the user is greater than the configured range.
		 * Expected response is 416.
		 */
		String uri = "/fileContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		String expectedMessage = "The range is greater than 50";
		details.add("filename", "test1.txt");
		details.add("start", "1");
		details.add("stop", "80");
	
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(416, status);
        String responseMessage = mvcResult.getResolvedException().getMessage();
        assertEquals(expectedMessage, responseMessage);
	}
	
	public void saveNonExistentFile() throws Exception {
		/*
		 * Tries to save a non exsisting file.
		 * Status expected 400.
		 */
		String uri = "/fileContent/tasks/parse";
		File file = new File(getClass().getResource("JSON_Inputs/NonExistentFile").getFile());
		String content = new String(Files.readAllBytes(file.toPath()));
		String expectedMessage = "The file with name NonExistentFile Does not exist";			
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);	
		String responseMessage = mvcResult.getResolvedException().getMessage();
		assertEquals(expectedMessage, responseMessage);
        
	}
	
}
