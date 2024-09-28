package org.jenie.spring.helloworld.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	BOARD_NOT_FOUND("Invalid board", 1, HttpStatus.BAD_REQUEST,
			"Error when the requested board is not found. The board is undefined, or an invalid board ID is used."),
	UNKNOWN("Unknown error", -1, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");

	private final String title;

	private final int code;

	private final String desc;

	private final HttpStatus httpStatus;

	ErrorCode(String title, int code, HttpStatus httpStatus, String desc) {
		this.title = title;
		this.code = code;
		this.httpStatus = httpStatus;
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

	public String getDesc() {
		return this.desc;
	}

}
