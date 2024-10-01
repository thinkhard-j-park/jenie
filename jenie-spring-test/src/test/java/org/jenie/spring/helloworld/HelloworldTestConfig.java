package org.jenie.spring.helloworld;

import org.jenie.spring.helloworld.operation.ArticleOperation;
import org.jenie.spring.test.client.HttpClient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HelloworldTestProperties.class)
public class HelloworldTestConfig {

	@Bean
	ArticleOperation articleOperation(HelloworldTestProperties testProperties) {
		return new ArticleOperation(HttpClient.restClient(testProperties.getClientName(), testProperties.getBaseUrl()));
	}

}
