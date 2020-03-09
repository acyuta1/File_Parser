package com.example.File_Parser.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.scheduling.annotation.Async;

import com.example.File_Parser.model.File_Content;

public interface FileContentRepository extends CassandraRepository<File_Content, String>{
//	List<File_Content> findByFile_Name(String file_name);


//	List<File_Content> findByfile_name(String file_name);
	
	@Query("SELECT * FROM File_Content where file_name=?0 and line_num=?1")
	File_Content findByfile_nameAndLine_Num(String file_name,int line_num);

}
