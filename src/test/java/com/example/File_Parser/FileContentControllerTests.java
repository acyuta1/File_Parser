package com.example.File_Parser;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import com.example.File_Parser.model.FilePath;
import com.example.File_Parser.model.FileTrackStatus;
import com.example.File_Parser.model.File_Content;
import com.example.File_Parser.model.File_Tracking;

public class FileContentControllerTests extends DemoApplicationTests {
	
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void saveFileContent() throws Exception {
		
		String uri = "/store-in-db";
		FilePath file_path = new FilePath();
		// Already Exists
		file_path.setFilepath("C:\\Users\\achyutha.aluru\\Desktop\\Files\\test1.txt");
		file_path.setBatch_size(10000);
		String input = super.mapToJson(file_path);
		System.out.println(input);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(input)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		
	}
	
	@Test
	public void getTrackStatus() throws Exception {
		String uri = "/getStatus/2";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		File_Tracking file_track_details = super.mapFromJson(content, File_Tracking.class);
		assertTrue(file_track_details!=null);
	}
	
	@Test
	public void getContent() throws Exception {
		String uri = "/getContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		details.add("filename", "newfile_short.txt");
		details.add("start", "2");
		details.add("stop", "8");
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
//        String content = mvcResult.getResponse().getContentAsString();
//        File_Content[] lines = super.mapFromJson(content, File_Content[].class);
//		assertTrue(lines.length > 0);
        

	}

}
