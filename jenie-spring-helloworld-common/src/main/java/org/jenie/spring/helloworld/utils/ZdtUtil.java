package org.jenie.spring.helloworld.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.protobuf.Timestamp;

public final class ZdtUtil {

	private ZdtUtil() {
	}

	public static ZonedDateTime fromTimestamp(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}

		var instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

	}

	public static Timestamp toTimestamp(final ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}

		return Timestamp.newBuilder().setSeconds(zonedDateTime.toEpochSecond()).build();
	}

	public static String zdtNowString() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

}
