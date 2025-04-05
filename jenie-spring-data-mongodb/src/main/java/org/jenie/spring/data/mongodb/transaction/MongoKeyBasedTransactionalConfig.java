package org.jenie.spring.data.mongodb.transaction;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouterConfig;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ExcludeCodeCoverageGenerated
@Configuration
@AutoConfigureAfter(MongoTemplateRouterConfig.class)
public class MongoKeyBasedTransactionalConfig {

	@Bean
	MongoKeyBasedTransactionAspect mongoKeyBasedTransactionAspect(MongoTemplateRouter mongoTemplateRouter) {
		return new MongoKeyBasedTransactionAspect(mongoTemplateRouter);
	}

}
