package com.example.File_Parser.model;

public class FilePath {

	private String filepath;
	private int batch_size;
	private int retrieve_size;
	
	public FilePath() {
		// TODO Auto-generated constructor stub
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public int getBatch_size() {
		return batch_size;
	}

	public void setBatch_size(int batch_size) {
		this.batch_size = batch_size;
	}

	public int getRetrieve_size() {
		return retrieve_size;
	}

	public void setRetrieve_size(int retrieve_size) {
		this.retrieve_size = retrieve_size;
	}

}
