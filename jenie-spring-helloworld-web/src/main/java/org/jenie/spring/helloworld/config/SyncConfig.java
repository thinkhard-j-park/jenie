package org.jenie.spring.helloworld.config;

import org.jenie.spring.helloworld.annotation.ConditionalOnSync;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ExcludeCodeCoverageGenerated
@ConditionalOnSync
@Configuration
@EnableMongoRepositories(basePackages = "org.jenie.spring.helloworld.repository.sync")
public class SyncConfig {

}
