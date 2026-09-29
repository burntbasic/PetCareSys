package com.petcare.exception;

public class DatabaseConfigException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DatabaseConfigException(String message) {
		super(message);
	}
	
	public DatabaseConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
