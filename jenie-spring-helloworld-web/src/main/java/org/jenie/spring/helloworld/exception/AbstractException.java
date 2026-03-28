package org.jenie.spring.helloworld.exception;

import org.jenie.spring.helloworld.dto.ErrorCode;

public abstract sealed class AbstractException extends RuntimeException
		permits CommonErrors.IllegalDataException, ArticleErrors.ArticleNotFoundException,
		ArticleErrors.ArticleModifyException, ArticleErrors.ArticleDeleteException, BoardErrors.BoardNotFoundException {

	private final ErrorCode errorCode;

	public AbstractException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

}
