package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.helloworld.dto.ErrorType;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorTypeController {

	@GetMapping("/{errorCode}")
	public Mono<ErrorType> getErrorType(@PathVariable int errorCode) {
		return Mono.just(ErrorCode.fromCode(errorCode).toErrorType());

	}

}
