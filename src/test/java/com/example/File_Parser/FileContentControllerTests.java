package com.example.File_Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import com.example.File_Parser.controller.FileContentService;
import com.example.File_Parser.model.FilePath;
import com.example.File_Parser.model.File_Tracking;

public class FileContentControllerTests extends DemoApplicationTests {
	
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}
	
	int id;
	
	@Test
	public void saveNewFileContent() throws Exception {
		/*
		 * Tries to save a new file. If successful,
		 * HTTP response 200 is expected.
		 */
		String uri = "/store-in-db";
		FilePath file_path = new FilePath();
		file_path.setFilepath("C:\\Users\\achyutha.aluru\\Desktop\\Files\\test1.txt");
		file_path.setBatch_size(10000);
		file_path.setRetrieve_size(50);
		String input = super.mapToJson(file_path);
//		System.out.println(input);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(input)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String json_content = mvcResult.getResponse().getContentAsString();
		File_Tracking file_details = super.mapFromJson(json_content, File_Tracking.class);
		id = file_details.getId();
	}
	
	@Test
	public void saveExistingFileContent() throws Exception {
		/*
		 * Tries to save the same file as above. 
		 * Expected response is 400.
		 */
		String uri = "/store-in-db";
		FilePath file_path = new FilePath();
		file_path.setFilepath("C:\\Users\\achyutha.aluru\\Desktop\\Files\\test1.txt");
		file_path.setBatch_size(10000);
		file_path.setRetrieve_size(50);
		String input = super.mapToJson(file_path);
//		System.out.println(input);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(input)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);	
	}
	
	
	@Test
	public void getTrackStatus() throws Exception {
		/*
		 * Tries to get the tracking status of a particular file upload.
		 * Expected response is 200 if successful.
		 */
		String uri = "/getStatus/"+id;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void getContent() throws Exception {
		/*
		 * Tries to retrieve a set of lines of a particular file present in cassandra database.
		 * Expected response is 200.
		 */
		String uri = "/getContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		FileContentService service = new FileContentService();
		service.initializeVars("newfile_short.txt", 10, 10);
		details.add("filename", "newfile_short.txt");
		details.add("start", "2");
		details.add("stop", "8");
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        
	}
	@Test
	public void getContentWhenRangeIsGreater() throws Exception {
		/*
		 * Tries to retrieve a set of lines of a particular file present in cassandra database.
		 * This time, the range provided by the user is greater than the configured range.
		 * Expected response is 416.
		 */
		String uri = "/getContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		FileContentService service = new FileContentService();
		service.initializeVars("newfile_short.txt", 10, 10);
		details.add("filename", "newfile_short.txt");
		details.add("start", "2");
		details.add("stop", "50");
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(416, status);
        
	}

}
