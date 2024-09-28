package org.jenie.spring.helloworld.exception;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(AbstractException.class)
	private ResponseEntity<ProblemDetail> handleControllerException(AbstractException ex, WebRequest request) {
		var httpServletRequest = ((ServletRequestAttributes) request).getRequest();
		var scheme = httpServletRequest.getScheme();
		var host = httpServletRequest.getServerName();
		var port = httpServletRequest.getServerPort();
		var requestUri = httpServletRequest.getRequestURI();
		var fullUrl = scheme + "://" + host + ":" + port + requestUri;
		var method = httpServletRequest.getMethod();
		var path = httpServletRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		var errorCode = ex.getErrorCode();
		var errorUrl = scheme + "://" + host + ":" + port + "/error/" + errorCode.getCode();

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
		problemDetail.setTitle(errorCode.getTitle());
		problemDetail.setType(URI.create(errorUrl));
		problemDetail.setInstance(URI.create(fullUrl));
		problemDetail.setProperty("method", method);
		problemDetail.setProperty("path", path);

		return ResponseEntity.status(errorCode.getHttpStatus()).body(problemDetail);
	}

}
