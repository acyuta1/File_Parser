package com.example.File_Parser.model;



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
		this.setFile_name(file_name);
		this.setLine_num(line_num);
		this.setLine(line);
	}


	public int getLine_num() {
		return line_num;
	}


	public String getFile_name() {
		return file_name;
	}


	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}


	public void setLine_num(int line_num) {
		this.line_num = line_num;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}

}
