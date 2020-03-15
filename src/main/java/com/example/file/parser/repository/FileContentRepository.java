package com.example.file.parser.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.example.file.parser.model.FileContent;

public interface FileContentRepository extends CassandraRepository<FileContent, String>{
//	List<File_Content> findByFile_Name(String file_name);


//	List<File_Content> findByfile_name(String file_name);
	
//	@Query("SELECT * FROM File_Content where file_name=?0 and line_num=?1")
//	FileContent findByfile_nameAndLine_Num(String file_name,int line_num);
	
	FileContent[] findByFileNameAndLineNumBetween(String fileName, int start, int stop);

}
