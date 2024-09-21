package org.jenie.spring.test.data.mongodb.operation;

import org.jenie.spring.test.data.mongodb.config.MongoDBConfig;
import org.jenie.spring.test.data.mongodb.config.MongoDBConnectorRegistry;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(MongoDBConfig.class)
public class MongoTemplateRouterConfig {

	@Bean
	@ConditionalOnMissingBean(MongoTemplateRouter.class)
	public MongoTemplateRouter mongoTemplateRouter(MongoDBConnectorRegistry registry) {
		return new MongoTemplateRouter(registry);
	}

}
