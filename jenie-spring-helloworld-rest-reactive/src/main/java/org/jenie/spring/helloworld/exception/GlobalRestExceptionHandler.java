package org.jenie.spring.helloworld.exception;

import java.net.URI;

import reactor.core.publisher.Mono;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
public class GlobalRestExceptionHandler {

	@ExceptionHandler(AbstractException.class)
	public Mono<ResponseEntity<ProblemDetail>> handleControllerException(AbstractException ex,
			ServerWebExchange exchange) {
		var request = exchange.getRequest();
		var scheme = request.getURI().getScheme();
		var host = request.getURI().getHost();
		var port = request.getURI().getPort();
		var requestUri = request.getPath().pathWithinApplication().value();
		var fullUrl = scheme + "://" + host + ((port != -1) ? ":" + port : "") + requestUri;
		var method = request.getMethod();
		var path = exchange.getAttributeOrDefault(
				"org.springframework.web.reactive.handler.PathPatternRouteMatcher.BEST_MATCHING_PATTERN", "");

		var errorCode = ex.getErrorCode();
		var errorUrl = scheme + "://" + host + ((port != -1) ? ":" + port : "") + "/error/" + errorCode.getCode();

		var problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
		problemDetail.setTitle(errorCode.getTitle());
		problemDetail.setType(URI.create(errorUrl));
		problemDetail.setInstance(URI.create(fullUrl));
		problemDetail.setProperty("error-code", String.valueOf(errorCode.getCode()));
		problemDetail.setProperty("method", method);
		problemDetail.setProperty("path", path);

		return Mono.just(ResponseEntity.status(errorCode.getHttpStatus()).body(problemDetail));
	}

}
