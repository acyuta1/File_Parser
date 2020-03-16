package com.example.file.parser.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.example.file.parser.model.FileContent;

public interface FileContentRepository extends CassandraRepository<FileContent, String>{
	
	FileContent[] findByFileNameAndLineNumBetween(String fileName, int start, int stop);
	
}
