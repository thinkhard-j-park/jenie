package org.jenie.spring.helloworld.exception;

import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public final class AssertHelper {

	@ExcludeCodeCoverageGenerated
	private AssertHelper() {
	}

	public static void hasText(@Nullable String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message);
		}
	}

	public static void notNull(@Nullable Object object, String message) {
		if (object == null) {
			throw new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message);
		}
	}

}
