package org.jenie.spring.data.mongodb.config;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouterConfig;
import org.jenie.spring.data.mongodb.transaction.MongoKeyBasedTransactionalConfig;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ExcludeCodeCoverageGenerated
@EnableConfigurationProperties(MongoDBSetting.class)
@Configuration
public class MongoDBAutoConfig {

	@ConditionalOnProperty(prefix = "mongodb.setting", name = "enabled", havingValue = "true")
	@Configuration
	@Import({ MongoDBConfig.class, MongoTemplateRouterConfig.class, MongoKeyBasedTransactionalConfig.class })
	static class MongoDBAutoConfigEnabler {

	}

}
