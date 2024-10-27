package org.jenie.spring.data.mongodb.transaction;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouterConfig;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ExcludeCodeCoverageGenerated
@Configuration
@AutoConfigureAfter(MongoTemplateRouterConfig.class)
@ComponentScan(basePackageClasses = MongoKeyBasedTransactionAspect.class)
public class MongoKeyBasedTransactionalConfig {

}
