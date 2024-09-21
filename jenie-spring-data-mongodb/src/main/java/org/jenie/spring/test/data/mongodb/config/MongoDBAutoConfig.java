package org.jenie.spring.test.data.mongodb.config;

import org.jenie.spring.test.data.mongodb.operation.MongoTemplateRouterConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties(MongoDBSetting.class)
@Configuration
public class MongoDBAutoConfig {

	@ConditionalOnProperty(prefix = "mongodb.setting", name = "enabled", havingValue = "true")
	@Configuration
	@Import({ MongoDBConfig.class, MongoTemplateRouterConfig.class })
	static class MongoDBAutoConfigEnabler {

	}

}
