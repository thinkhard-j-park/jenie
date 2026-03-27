package org.jenie.spring.helloworld.exception;

import java.net.URI;

import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.helloworld.exception.CommonErrors.IllegalDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalRestExceptionHandlerTests {

	@InjectMocks
	private GlobalRestExceptionHandler globalExceptionHandler;

	@Test
	void handleControllerExceptionTest() {
		// given
		var httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setScheme("http");
		httpServletRequest.setServerName("localhost");
		httpServletRequest.setServerPort(30000);
		httpServletRequest.setRequestURI("/jenie-test/article/1");
		httpServletRequest.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/{service}/article/{id}");
		httpServletRequest.setMethod("GET");
		var request = new ServletWebRequest(httpServletRequest);

		AbstractException abstractException = new IllegalDataException(ErrorCode.ILLEGAL_DATA, "id is required");

		// when
		ResponseEntity<ProblemDetail> responseEntity = this.globalExceptionHandler
			.handleControllerException(abstractException, request);

		// then
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		ProblemDetail problemDetail = responseEntity.getBody();
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getTitle()).isEqualTo(ErrorCode.ILLEGAL_DATA.getTitle());
		assertThat(problemDetail.getDetail()).isEqualTo("id is required");
		assertThat(problemDetail.getType())
			.isEqualTo(URI.create("http://localhost:30000/error/" + ErrorCode.ILLEGAL_DATA.getCode()));
		assertThat(problemDetail.getInstance()).isEqualTo(URI.create("http://localhost:30000/jenie-test/article/1"));
		assertThat(problemDetail.getProperties()).isNotNull();
		assertThat(problemDetail.getProperties().get("method")).isEqualTo(HttpMethod.GET.name());
		assertThat(problemDetail.getProperties().get("path")).isEqualTo("/{service}/article/{id}");
	}

}
