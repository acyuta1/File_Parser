package com.example.File_Parser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class RangeOutOfBounds extends RuntimeException {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7744183672349860733L;

	public RangeOutOfBounds() {}
	
	public RangeOutOfBounds(String message) {
		super(message);
	}
	
}

