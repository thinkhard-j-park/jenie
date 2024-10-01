package org.jenie.spring.helloworld.exception;

public class CommonErrors {

	public static class IllegalDataException extends AbstractException {

		public IllegalDataException(ErrorCode errorCode, String message) {
			super(errorCode, message);
		}

	}

}
