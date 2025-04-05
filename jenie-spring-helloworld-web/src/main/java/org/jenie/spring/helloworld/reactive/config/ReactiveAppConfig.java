package org.jenie.spring.helloworld.reactive.config;

import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@ConditionalOnReactive
@Configuration
@EnableReactiveMongoRepositories(basePackages = "org.jenie.spring.helloworld.reactive.repository")
public class ReactiveAppConfig {

}
