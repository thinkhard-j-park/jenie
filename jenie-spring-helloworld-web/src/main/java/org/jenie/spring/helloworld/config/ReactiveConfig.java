package org.jenie.spring.helloworld.config;

import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@ConditionalOnReactive
@Configuration
@EnableReactiveMongoRepositories(basePackages = "org.jenie.spring.helloworld.repository.reactive")
public class ReactiveConfig {

}
