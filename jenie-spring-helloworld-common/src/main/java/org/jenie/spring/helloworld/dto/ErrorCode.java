package org.jenie.spring.helloworld.dto;

import io.grpc.Status;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	//@formatter:off
	ILLEGAL_DATA("Invalid data",
			1, HttpStatus.BAD_REQUEST, Status.Code.INVALID_ARGUMENT,
			"Invalid data is used"),

	BOARD_NOT_FOUND("Invalid board",
			100, HttpStatus.BAD_REQUEST, Status.Code.INVALID_ARGUMENT,
			"Error when the requested board is not found. The board is undefined, or an invalid board ID is used."),

	ARTICLE_NOT_FOUND("Invalid article",
			200, HttpStatus.BAD_REQUEST, Status.Code.INVALID_ARGUMENT,
			"Error when the requested article is not found. Invalid article ID is used"),

	ARTICLE_MODIFY_NOT_ALLOWED("Not allowed modify article",
			201, HttpStatus.METHOD_NOT_ALLOWED, Status.Code.PERMISSION_DENIED,
			"Error when modifying a article is not allowed. It may happen if an invalid writer is used to modify article."),

	UNKNOWN("Unknown error",
			-1, HttpStatus.INTERNAL_SERVER_ERROR, Status.Code.INTERNAL,
			"An unexpected error occurred.");
	//@formatter:on

	private final String title;

	private final int code;

	private final HttpStatus httpStatus;

	private final Status.Code grpcStatus;

	private final String desc;

	ErrorCode(String title, int code, HttpStatus httpStatus, Status.Code grpcStatus, String desc) {
		this.title = title;
		this.code = code;
		this.httpStatus = httpStatus;
		this.grpcStatus = grpcStatus;
		this.desc = desc;
	}

	public static ErrorCode fromCode(int errorCode) {
		for (ErrorCode errorCodeEnum : values()) {
			if (errorCodeEnum.getCode() == errorCode) {
				return errorCodeEnum;
			}
		}
		return UNKNOWN;
	}

	public ErrorType toErrorType() {
		return new ErrorType(this.name(), this.title, this.code, this.desc, this.httpStatus);
	}

	public String getTitle() {
		return this.title;
	}

	public int getCode() {
		return this.code;
	}

	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	public Status.Code getGrpcStatus() {
		return this.grpcStatus;
	}

	public String getDesc() {
		return this.desc;
	}

}
