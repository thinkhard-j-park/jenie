package org.jenie.spring.helloworld.config;

import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ExcludeCodeCoverageGenerated
@Configuration
@EnableMongoRepositories(basePackages = "org.jenie.spring.helloworld.repository")
public class MongoDBConfiguration {

}
