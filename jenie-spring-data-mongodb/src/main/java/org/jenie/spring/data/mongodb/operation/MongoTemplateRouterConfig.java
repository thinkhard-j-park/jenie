package org.jenie.spring.data.mongodb.operation;

import org.jenie.spring.data.mongodb.config.MongoDBConfig;
import org.jenie.spring.data.mongodb.config.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.config.MongoDBSetting;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@ExcludeCodeCoverageGenerated
@Configuration
@AutoConfigureAfter(MongoDBConfig.class)
public class MongoTemplateRouterConfig {

	@Bean
	@ConditionalOnMissingBean(MongoTemplateRouter.class)
	public MongoTemplateRouter mongoTemplateRouter(MongoDBSetting setting, MongoDBConnectorRegistry registry) {
		if (StringUtils.hasText(setting.getRouterType()) && setting.getRouterType().equalsIgnoreCase("simple")) {
			return new SimpleMongoTemplateRouter(registry);
		}
		return new CaffeineMongoTemplateRouter(registry);
	}

}
