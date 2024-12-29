package org.jenie.spring.helloworld;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.jenie.spring.client.HttpClient;
import org.jenie.spring.helloworld.grpc.ArticleServiceGrpc;
import org.jenie.spring.helloworld.operation.ArticleGrpcOperation;
import org.jenie.spring.helloworld.operation.ArticleRestOperation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ GrpcClientAutoConfiguration.class })
@Configuration
@EnableConfigurationProperties(HelloworldTestProperties.class)
@GrpcClientBean(clazz = ArticleServiceGrpc.ArticleServiceBlockingStub.class, beanName = "blockingStub",
		client = @GrpcClient("helloworld"))
public class HelloworldTestConfig {

	@Bean
	ArticleRestOperation articleRestOperation(HelloworldTestProperties testProperties) {
		return new ArticleRestOperation(
				HttpClient.restClient(testProperties.getClientName(), testProperties.getBaseUrl()));
	}

	@Bean
	ArticleGrpcOperation articleGrpcOperation(ArticleServiceGrpc.ArticleServiceBlockingStub blockingStub) {
		return new ArticleGrpcOperation(blockingStub);
	}

}
