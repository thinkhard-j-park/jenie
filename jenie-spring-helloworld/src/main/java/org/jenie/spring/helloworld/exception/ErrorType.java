package org.jenie.spring.helloworld.exception;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드와 이것과 연결된 HttpStatus 을 rest api 응답으로 정의한다. RFC 9457 에 정의된 'type' URI 에 대한 응답을 제공하는
 * 클래스이다. {@link org.jenie.spring.helloworld.exception.ErrorCode} 을 json 형태로 변환한다.
 *
 * @param name 에러 코드 이름
 * @param title 에러 타이틀
 * @param errorCode 에러 코드
 * @param description 에러에 대한 상세 살명
 * @param httpStatus 에러 코드와 연결된 Http 상태 코드
 * <a href="https://datatracker.ietf.org/doc/html/rfc9457}">Problem Detail</a> 의 내용을 참고한다.
 * @author thinkardj thinkhard.j.park@gmail.com
 */

public record ErrorType(String name, String title, int errorCode, String description, HttpStatus httpStatus) {
}
