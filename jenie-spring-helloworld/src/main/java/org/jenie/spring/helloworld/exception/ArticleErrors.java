package org.jenie.spring.helloworld.exception;

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

}
