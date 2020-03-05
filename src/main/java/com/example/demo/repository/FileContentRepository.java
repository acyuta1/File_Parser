package com.example.demo.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

//import com.example.demo.model.FileContent;
//import com.example.demo.model.User;
import com.example.demo.model.File_Content;

public interface FileContentRepository extends CassandraRepository<File_Content, String>{

//	List<File_Content> findByFileName(String file_name);

//	List<File_Content> findByfile_name(String file_name);

}
