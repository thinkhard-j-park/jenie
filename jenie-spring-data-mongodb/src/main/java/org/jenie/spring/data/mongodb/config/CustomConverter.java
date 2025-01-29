package org.jenie.spring.data.mongodb.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class CustomConverter {

	static class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {

		@Override
		public ZonedDateTime convert(Date source) {
			return source.toInstant().atZone(ZoneId.systemDefault());
		}

	}

	static class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {

		@Override
		public Date convert(ZonedDateTime source) {
			return Date.from(source.toInstant());
		}

	}

}
