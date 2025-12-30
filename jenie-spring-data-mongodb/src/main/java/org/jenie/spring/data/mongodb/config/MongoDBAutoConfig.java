package org.jenie.spring.data.mongodb.config;

import com.mongodb.client.MongoClient;
import org.jenie.spring.data.mongodb.connector.MongoDBSetting;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouterConfig;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouterConfig;
import org.jenie.spring.data.mongodb.transaction.MongoKeyBasedTransactionalConfig;
import org.jenie.spring.data.mongodb.transaction.ReactiveMongoKeyBasedTransactionalConfig;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ExcludeCodeCoverageGenerated
@EnableConfigurationProperties(MongoDBSetting.class)
@AutoConfiguration
public class MongoDBAutoConfig {

	private static final String MONGODB_SETTING_PREFIX = "mongodb.setting";

	private static final String PROPERTY_ENABLED = "enabled";

	private static final String PROPERTY_VALUE_TRUE = "true";

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(prefix = MONGODB_SETTING_PREFIX, name = PROPERTY_ENABLED, havingValue = PROPERTY_VALUE_TRUE)
	@ConditionalOnClass(MongoClient.class)
	@Import({ MongoDBConfig.class, MongoTemplateRouterConfig.class, MongoKeyBasedTransactionalConfig.class })
	static class MongoDBAutoConfigEnabler {

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(prefix = MONGODB_SETTING_PREFIX, name = PROPERTY_ENABLED, havingValue = PROPERTY_VALUE_TRUE)
	@ConditionalOnClass(com.mongodb.reactivestreams.client.MongoClient.class)
	@Import({ ReactiveMongoDBConfig.class, ReactiveMongoTemplateRouterConfig.class,
			ReactiveMongoKeyBasedTransactionalConfig.class })
	static class ReactiveMongoDBAutoConfigEnabler {

	}

}
