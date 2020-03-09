package com.example.File_Parser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class RangeOutOfBounds extends RuntimeException {

	
	/**
	 * 
	 */
	

	public RangeOutOfBounds() {}
	
	public RangeOutOfBounds(String message) {
		super(message);
	}
	
}

