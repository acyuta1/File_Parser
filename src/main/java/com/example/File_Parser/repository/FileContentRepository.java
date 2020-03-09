package com.example.File_Parser.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.scheduling.annotation.Async;

import com.example.File_Parser.model.File_Content;

public interface FileContentRepository extends CassandraRepository<File_Content, String>{
//	List<File_Content> findByFileName(String file_name);

//	List<File_Content> findByfile_name(String file_name);

}
