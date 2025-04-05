package org.jenie.spring.data.mongodb.transaction;

import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouterConfig;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ReactiveMongoTemplateRouterConfig.class)
public class ReactiveMongoKeyBasedTransactionalConfig {

	@Bean
	ReactiveMongoKeyBasedTransactionAspect reactiveMongoKeyBasedTransactionAspect(
			ReactiveMongoTemplateRouter reactiveMongoTemplateRouter) {
		return new ReactiveMongoKeyBasedTransactionAspect(reactiveMongoTemplateRouter);
	}

}
