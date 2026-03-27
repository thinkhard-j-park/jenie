package org.jenie.spring.helloworld.exception;

import org.jenie.spring.helloworld.dto.ErrorCode;

public class ArticleErrors {

	public static final class ArticleNotFoundException extends AbstractException {

		public ArticleNotFoundException(ErrorCode code, String message) {
			super(code, message);
		}

	}

	public static final class ArticleModifyException extends AbstractException {

		public ArticleModifyException(ErrorCode code, String message) {
			super(code, message);
		}

	}

	public static final class ArticleDeleteException extends AbstractException {

		public ArticleDeleteException(ErrorCode code, String message) {
			super(code, message);
		}

	}

}
