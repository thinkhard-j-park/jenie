package org.jenie.spring.helloworld.exception;

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
