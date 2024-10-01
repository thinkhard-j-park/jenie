package org.jenie.spring.helloworld.exception;

public class BoardErrors {

	public static class BoardNotFoundException extends AbstractException {

		public BoardNotFoundException(ErrorCode code, String message) {
			super(code, message);
		}

	}

}
