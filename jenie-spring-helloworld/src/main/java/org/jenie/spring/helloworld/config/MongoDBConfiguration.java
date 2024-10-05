package org.jenie.spring.helloworld.config;

import org.jenie.spring.data.mongodb.config.MongoDBAutoConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.jenie.spring.helloworld.repository")
@Import(MongoDBAutoConfig.class)
public class MongoDBConfiguration {

}
