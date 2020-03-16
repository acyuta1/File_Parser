package com.example.file.parser.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.file.parser.DemoApplicationTests;

public class FileTrackingControllerTests extends DemoApplicationTests {
	
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
		File file = new File(getClass().getResource("../resources/TrackStatusOutput").getFile());
		String content = new String(Files.readAllBytes(file.toPath()));
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		
		String responseJson = mvcResult.getResponse().getContentAsString();
		assertEquals(content, responseJson);
	}
	
	
}
