package org.jenie.spring.test.data.mongodb.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ObjectUtils;

@Configuration
public abstract class MongoDBConfig extends MongoConfigurationSupport {

	@Bean
	public MongoDBConnectorRegistry mongoDBConnectorRegistry(MongoDBSetting setting, MongoMappingContext mappingContext,
			MongoCustomConversions customConversions) {

		var router = new MongoDBConnectorRegistry();
		for (String clusterKey : setting.getCluster().keySet()) {
			var cluster = setting.getCluster().get(clusterKey);
			if (ObjectUtils.isEmpty(cluster.getAppName())) {
				cluster.setAppName(setting.getAppName());
			}
			var connector = new MongoDBConnector(cluster, mappingContext, customConversions);
			router.addConnector(clusterKey, connector);
		}

		return router;
	}

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		var converters = new ArrayList<Converter<?, ?>>();
		converters.add(new ZonedDateTimeReadConverter());
		converters.add(new ZonedDateTimeWriteConverter());

		return new MongoCustomConversions(converters);

	}

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
