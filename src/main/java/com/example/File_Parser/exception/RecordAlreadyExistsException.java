package com.example.File_Parser.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecordAlreadyExistsException extends RuntimeException {

	
	/**
	 * 
	 */
	

	public RecordAlreadyExistsException() {}
	
	public RecordAlreadyExistsException(String fileName, int id) {
		super("File with fileName "+fileName+" and ID "+id+" Already exists");
	}
	
}
