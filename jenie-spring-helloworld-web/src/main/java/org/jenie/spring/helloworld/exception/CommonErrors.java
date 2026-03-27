package org.jenie.spring.helloworld.exception;

import org.jenie.spring.helloworld.dto.ErrorCode;

public class CommonErrors {

	public static final class IllegalDataException extends AbstractException {

		public IllegalDataException(ErrorCode errorCode, String message) {
			super(errorCode, message);
		}

	}

}
