package com.example.file.parser.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception to be thrown when a non existing file is provided.
 * @author Achyutha.aluru
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileDoesNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8036878919802524764L;
	
	public FileDoesNotExistException() {}
	
	public FileDoesNotExistException(String fileName) {
		super("The file with name " + fileName + " Does not exist");
	}
	

}
