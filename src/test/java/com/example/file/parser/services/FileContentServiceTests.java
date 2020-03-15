package com.example.file.parser.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;

import com.example.file.parser.DemoApplicationTests;
import com.example.file.parser.exception.RangeOutOfBoundsException;
import com.example.file.parser.model.FileContent;
import com.example.file.parser.model.Filetrack;
import com.example.file.parser.repository.FileContentRepository;
import com.example.file.parser.services.FileContentService;

//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
public class FileContentServiceTests {

	
	@InjectMocks
	FileContentService fileContentService;
	
	@Mock
	FileContentRepository repository;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void when_insert_it_should_return_fileName() {
		FileContent fileContent = new FileContent();
		fileContent.setFileName("test.txt");
		fileContent.setLineNum(1);
		fileContent.setLine("test line");
		List<FileContent> fileContentArray = new ArrayList<>();
		fileContentArray.add(fileContent);
		String fileName = fileContentService.insertIntoFileContent(fileContentArray);
		assertThat(fileName).isSameAs(fileContent.getFileName());
	}
	
	@Test
	public void when_total_and_batch_provided_should_return_percentage() {
		assertEquals(fileContentService.calculateRemaining(100, 100),(100.0f));
	}
	
	@Test
	public void when_range_greater_throw_exception() {
	
		assertThatThrownBy(()-> fileContentService.retrieveContent("test",1, 10, 3 ))
				.isInstanceOf(RangeOutOfBoundsException.class);
	}
	
	
	
}
