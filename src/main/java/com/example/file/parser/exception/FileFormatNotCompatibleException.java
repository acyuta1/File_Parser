package com.example.file.parser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileFormatNotCompatibleException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4476008459574817440L;

	public FileFormatNotCompatibleException(String fileName) {
		super("File format is not .txt of file: "+fileName);
	}

}
