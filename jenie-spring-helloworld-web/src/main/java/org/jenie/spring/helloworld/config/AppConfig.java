package org.jenie.spring.helloworld.config;

import org.jenie.spring.helloworld.annotation.ConditionalOnImperative;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ExcludeCodeCoverageGenerated
@ConditionalOnImperative
@Configuration(proxyBeanMethods = false)
@EnableMongoRepositories(basePackages = "org.jenie.spring.helloworld.repository")
public class AppConfig {

}
