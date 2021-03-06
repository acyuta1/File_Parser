package com.example.file.parser.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * FileContent table to store the content of a file.
 * @author Achyutha.aluru
 *
 */
@Table
public class FileContent {
	
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
	private String fileName;
	@PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
	private int lineNum;
	private String line;
	

	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public int getLineNum() {
		return lineNum;
	}


	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}


	

}
