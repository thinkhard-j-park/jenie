package org.jenie.spring.test.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ZdtUtil {

	private ZdtUtil() {
	}

	public static String zdtNowString() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

}
