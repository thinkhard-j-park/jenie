package org.jenie.spring.helloworld.exception;

import org.bson.types.ObjectId;
import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;
import org.jspecify.annotations.Nullable;

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

	public static void validObjectId(String id, String message) {
		AssertHelper.hasText(id, "id is required");
		if (!ObjectId.isValid(id)) {
			throw new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, message);
		}
	}

}
