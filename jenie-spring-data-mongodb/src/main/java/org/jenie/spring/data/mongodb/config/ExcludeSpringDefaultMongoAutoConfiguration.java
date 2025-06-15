package org.jenie.spring.data.mongodb.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = { MongoDBAutoConfig.class, MongoDataAutoConfiguration.class })
public class ExcludeSpringDefaultMongoAutoConfiguration {

}
