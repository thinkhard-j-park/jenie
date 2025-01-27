package org.jenie.spring.helloworld.exception;

import org.jenie.spring.helloworld.dto.ErrorCode;

public class ArticleErrors {

	public static class ArticleNotFoundException extends AbstractException {

		public ArticleNotFoundException(ErrorCode code, String message) {
			super(code, message);
		}

	}

	public static class ArticleModifyException extends AbstractException {

		public ArticleModifyException(ErrorCode code, String message) {
			super(code, message);
		}

	}

	public static class ArticleDeleteException extends AbstractException {

		public ArticleDeleteException(ErrorCode code, String message) {
			super(code, message);
		}

	}

}
