package com.example.File_Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.example.file.parser.model.FileTracking;
import com.example.file.parser.services.FileContentService;


public class FileContentServiceTests extends DemoApplicationTests {
	
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
		String uri = "/fileContent/tasks/parse";
		File file = ResourceUtils.getFile("C:\\Users\\achyutha.aluru\\Documents\\workspace-sts-3.9.11.RELEASE\\File_Parser\\src\\test\\java\\com\\example\\File_Parser\\Input");
		String content = new String(Files.readAllBytes(file.toPath()));
		File expectedOutputFile = ResourceUtils.getFile("C:\\Users\\achyutha.aluru\\Documents\\workspace-sts-3.9.11.RELEASE\\File_Parser\\src\\test\\java\\com\\example\\File_Parser\\ExpectedOutput");
		String expectedOuput = new String(Files.readAllBytes(expectedOutputFile.toPath()));
		
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String json_content = mvcResult.getResponse().getContentAsString();
		JSONAssert.assertEquals(expectedOuput, json_content, false);
		
	}
	
	@Test
	public void saveExistingFileContent() throws Exception {
		/*
		 * Tries to save the same file as above. 
		 * Expected response is 400.
		 */
		String uri = "/fileContent/tasks/parse";
		File file = ResourceUtils.getFile("C:\\Users\\achyutha.aluru\\Documents\\workspace-sts-3.9.11.RELEASE\\File_Parser\\src\\test\\java\\com\\example\\File_Parser\\Input");
		String content = new String(Files.readAllBytes(file.toPath()));
				
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);	
	}
	
	
	
//	
	@Test
	public void getContent() throws Exception {
		/*
		 * Tries to retrieve a set of lines of a particular file present in cassandra database.
		 * Expected response is 200.
		 */
		String uri = "/fileContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
		details.add("filename", "test1.txt");
		details.add("start", "1");
		details.add("stop", "3");
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
		String uri = "/fileContent";
		LinkedMultiValueMap<String,String> details = new LinkedMultiValueMap<>();
	
//		service.initializeVars("newfile_short.txt", 10, 10);
		details.add("filename", "test1.txt");
		details.add("start", "1");
		details.add("stop", "80");
	
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(details)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(416, status);
        
	}
	
	public void saveNonExistentFile() throws Exception {
		/*
		 * Tries to save a non exsisting file.
		 * Status expected 400.
		 */
		String uri = "/fileContent/tasks/parse";
		File file = ResourceUtils.getFile("C:\\Users\\achyutha.aluru\\Documents\\workspace-sts-3.9.11.RELEASE\\File_Parser\\src\\test\\java\\com\\example\\File_Parser\\NonExistentFile");
		String content = new String(Files.readAllBytes(file.toPath()));
				
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);	
        
	}
	
}
