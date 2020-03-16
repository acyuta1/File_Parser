package com.example.file.parser.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.file.parser.exception.RangeOutOfBoundsException;
import com.example.file.parser.model.FileContent;
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
	public void when_range_greater_throw_exception() {
	
		assertThatThrownBy(()-> fileContentService.retrieveContent("test",1, 10, 3 ))
				.isInstanceOf(RangeOutOfBoundsException.class);
	}
	
	
	
}
