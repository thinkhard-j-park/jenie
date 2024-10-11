package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ErrorTypeControllerTests {

	@InjectMocks
	private ErrorTypeController errorTypeController;

	@Test
	void getErrorType() {
		// given
		var errorCode = ErrorCode.ARTICLE_NOT_FOUND.getCode();

		// when
		var errorType = errorTypeController.getErrorType(errorCode);

		// then
		assertThat(errorType).isNotNull();
		assertThat(errorType.errorCode()).isEqualTo(errorCode);
		assertThat(errorType.title()).isEqualTo(ErrorCode.ARTICLE_NOT_FOUND.getTitle());
		assertThat(errorType.description()).isEqualTo(ErrorCode.ARTICLE_NOT_FOUND.getDesc());
		assertThat(errorType.httpStatus()).isEqualTo(ErrorCode.ARTICLE_NOT_FOUND.getHttpStatus());
	}

}
