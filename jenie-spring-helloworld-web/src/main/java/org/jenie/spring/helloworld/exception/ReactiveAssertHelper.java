package org.jenie.spring.helloworld.exception;

import org.bson.types.ObjectId;
import org.jenie.spring.helloworld.dto.ErrorCode;
import reactor.core.publisher.Mono;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public final class ReactiveAssertHelper {

	private ReactiveAssertHelper() {
	}

	public static Mono<String> hasText(@Nullable String text, String message) {
		if (!StringUtils.hasText(text)) {
			return Mono.error(new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message));
		}

		return Mono.just(text);
	}

	public static <T> Mono<T> notNull(@Nullable T object, String message) {
		if (object == null) {
			return Mono.error(new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message));
		}

		return Mono.just(object);
	}

	public static Mono<String> validObjectId(String id, String message) {
		return ReactiveAssertHelper.hasText(id, "id is required").flatMap((candidate) -> {
			if (!ObjectId.isValid(candidate)) {
				return Mono.error(new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message));
			}
			return Mono.just(candidate);
		});
	}

}
