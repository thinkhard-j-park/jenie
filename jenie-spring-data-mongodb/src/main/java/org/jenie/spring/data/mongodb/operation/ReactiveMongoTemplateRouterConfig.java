package org.jenie.spring.data.mongodb.operation;

import org.jenie.spring.data.mongodb.config.ReactiveMongoDBConfig;
import org.jenie.spring.data.mongodb.connector.MongoDBSetting;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnectorRegistry;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ReactiveMongoDBConfig.class)
public class ReactiveMongoTemplateRouterConfig {

	@Bean
	@ConditionalOnMissingBean(ReactiveMongoTemplateRouter.class)
	public ReactiveMongoTemplateRouter reactiveMongoTemplateRouter(MongoDBSetting setting,
			ReactiveMongoDBConnectorRegistry registry) {
		if (StringUtils.hasText(setting.getRouterType()) && setting.getRouterType().equalsIgnoreCase("simple")) {
			return new ReactiveSimpleMongoTemplateRouter(registry);
		}
		return new ReactiveCaffeineMongoTemplateRouter(registry);
	}

}
