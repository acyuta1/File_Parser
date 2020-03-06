package com.example.File_Parser.model;

import java.util.UUID;


import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File_Content {
	
	@PrimaryKey
	private String file_name;
	private int line_num;
	private String line;
	
	
	public File_Content(String file_name, int line_num, String line) {
		// TODO Auto-generated constructor stub
		this.file_name = file_name;
		this.line_num = line_num;
		this.line = line;
	}

}
