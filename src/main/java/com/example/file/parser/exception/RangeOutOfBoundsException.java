package com.example.file.parser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception to be thrown when the range provided is more than the
 * 	accepted range.
 * @author Achyutha.aluru
 */
@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class RangeOutOfBoundsException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7744183672349860733L;

	public RangeOutOfBoundsException(String message) {
		super(message);
	}
	
}

