package org.jenie.spring.data.mongodb.config;

import org.jenie.spring.data.mongodb.connector.MongoDBConnector;
import org.jenie.spring.data.mongodb.connector.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.connector.MongoDBSetting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

	@Override
	protected void configureConverters(
			MongoCustomConversions.MongoConverterConfigurationAdapter converterConfigurationAdapter) {
		converterConfigurationAdapter.registerConverter(new CustomConverter.ZonedDateTimeReadConverter());
		converterConfigurationAdapter.registerConverter(new CustomConverter.ZonedDateTimeWriteConverter());
	}

}
