package com.example.file.parser.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception to be thrown when duplicate file is inserted.
 * @author Achyutha.aluru
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecordAlreadyExistsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3583261722292568113L;

	public RecordAlreadyExistsException() {}
	
	public RecordAlreadyExistsException(String fileName, int id) {
		super("File with fileName "+fileName+" and ID "+id+" Already exists");
	}
	
}
