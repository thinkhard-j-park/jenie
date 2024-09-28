package org.jenie.spring.helloworld.exception;

import org.springframework.http.HttpStatus;

public record ErrorType(String name, String title, int errorCode, String description, HttpStatus httpStatus) {
}
