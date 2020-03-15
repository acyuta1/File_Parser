package com.example.file.parser.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.file.parser.exception.FileDoesNotExistException;

public class UtilityFunctions {
	Logger logger = LoggerFactory.getLogger(UtilityFunctions.class);

	
	public static int getFileDetails(String filepath) {
		File file = new File(filepath);
		int lineCount = 0;
		/*
		 * Checks if a file actually exists.
		 * If no, a custom exception exception with HTTP response 400 is thrown.
		 * 
		 */
		if(!(file.exists())) {
			throw new FileDoesNotExistException(filepath);
		} else {
			try {
				/*
				 *  Scanner object which reads a given file in 10MB chunks.
				 *  This is to avoid out of memory exceptions.
				 */
				FileInputStream stream = new FileInputStream(filepath);
				byte[] buffer = new byte[8192];
				int n;
				while((n= stream.read(buffer))>0) {
					for(int i =0; i<n;i++) {
						if(buffer[i]=='.') {
							lineCount++;
						}
					}
				}
			
			stream.close();
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		// The scanner object is returned. 
		return lineCount;
	}
	
	/**
	 * Method to return a scanner object.
	 * @param filepath
	 * @return Scanner object for the file provided. 
	 */
	public static Scanner scanFile (String filepath) {

		File file = new File(filepath);
		Scanner sc = null;
			try {
				/*
				 *  Scanner object which reads a given file in 10MB chunks.
				 *  This is to avoid out of memory exceptions.
				 */
				sc = new Scanner(new BufferedReader(new FileReader(file), 100*1024));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// The scanner object is returned. 
		return sc;
	}
}

