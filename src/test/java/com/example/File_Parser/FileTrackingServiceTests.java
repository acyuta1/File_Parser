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
import com.example.File_Parser.model.FileTracking;

public class FileTrackingServiceTests extends DemoApplicationTests {
	
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}
	
	int id;
	
	@Test
	public void getTrackStatus() throws Exception {
		/*
		 * Tries to get the tracking status of a particular file upload.
		 * Expected response is 200 if successful.
		 */
		String uri = "/fileTrack/getStatus/1";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	
}
