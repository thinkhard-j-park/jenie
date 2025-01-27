package org.jenie.spring.helloworld.dto;

import org.springframework.http.HttpStatus;

/**
 * Defines error code and corresponding HttpStatus as a REST API response. This class
 * provides responses for the 'type' URI defined in RFC 9457. Converts {@link ErrorCode}
 * into JSON format.
 *
 * @param name The name of the error code
 * @param title The title of the error
 * @param errorCode The error code
 * @param description Detailed description of the error
 * @param httpStatus HTTP status code associated with the error code Refer to
 * <a href="https://datatracker.ietf.org/doc/html/rfc9457">Problem Detail</a> for more
 * information.
 * @author thinkardj park
 */

public record ErrorType(String name, String title, int errorCode, String description, HttpStatus httpStatus) {
}
