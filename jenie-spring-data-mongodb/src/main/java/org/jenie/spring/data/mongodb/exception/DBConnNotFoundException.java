package org.jenie.spring.data.mongodb.exception;

public class DBConnNotFoundException extends RuntimeException {

	public DBConnNotFoundException(String message) {
		super(message);
	}

}
