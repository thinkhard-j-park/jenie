package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.exception.ErrorCode;
import org.jenie.spring.helloworld.exception.ErrorType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorTypeController {

	@GetMapping("/{errorCode}")
	public ErrorType problemDetail(@PathVariable int errorCode) {
		return ErrorCode.fromCode(errorCode).toErrorType();

	}

}
