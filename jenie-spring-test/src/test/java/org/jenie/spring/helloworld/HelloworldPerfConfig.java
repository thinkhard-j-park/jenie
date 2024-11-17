package org.jenie.spring.helloworld;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jenie.spring.data.mongodb.config.MongoDBAutoConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableEncryptableProperties
@EnableConfigurationProperties(HelloworldTestProperties.class)
@Import(MongoDBAutoConfig.class)
@ComponentScan(basePackages = { "org.jenie.spring.helloworld.repository" })
public class HelloworldPerfConfig {

}
