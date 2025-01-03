package org.jenie.spring.helloworld.exception;

import org.jenie.spring.helloworld.dto.ErrorCode;

public abstract class AbstractException extends RuntimeException {

	private final ErrorCode errorCode;

	public AbstractException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

}
