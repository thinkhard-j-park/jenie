package org.jenie.spring.helloworld;

import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import org.jenie.spring.client.HttpClient;
import org.jenie.spring.client.LogGrpcInterceptor;
import org.jenie.spring.helloworld.grpc.ArticleServiceGrpc;
import org.jenie.spring.helloworld.grpc.HelloServiceGrpc;
import org.jenie.spring.helloworld.grpc.ReactorArticleServiceGrpc;
import org.jenie.spring.helloworld.operation.ArticleGrpcOperation;
import org.jenie.spring.helloworld.operation.ArticleGrpcReactiveOperation;
import org.jenie.spring.helloworld.operation.ArticleRestOperation;
import org.jenie.spring.helloworld.operation.HelloGrpcOperation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(HelloworldTestProperties.class)
@Configuration(proxyBeanMethods = false)
public class HelloworldTestConfig {

	@Bean
	ArticleRestOperation articleRestOperation(HelloworldTestProperties testProperties) {
		return new ArticleRestOperation(
				HttpClient.restClient(testProperties.getClientName(), testProperties.getRestUrl()));
	}

	@Bean
	ArticleRestOperation articleRestReactiveOperation(HelloworldTestProperties testProperties) {
		return new ArticleRestOperation(
				HttpClient.restClient(testProperties.getClientName(), testProperties.getRestReactiveUrl()));
	}

	@Bean
	LogGrpcInterceptor logGrpcInterceptor(HelloworldTestProperties testProperties) {
		return new LogGrpcInterceptor(testProperties);
	}

	@Bean
	ArticleGrpcOperation articleGrpcOperation(HelloworldTestProperties testProperties,
			LogGrpcInterceptor logGrpcInterceptor) {
		var grpcClient = GrpcClients.builder(testProperties.getGrpcUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.intercept(logGrpcInterceptor)
			.build(ArticleServiceGrpc.ArticleServiceBlockingStub.class);
		return new ArticleGrpcOperation(grpcClient);
	}

	@Bean
	ArticleGrpcReactiveOperation articleGrpcReactiveOperation(HelloworldTestProperties testProperties,
			LogGrpcInterceptor logGrpcInterceptor) {
		var grpcClient = GrpcClients.builder(testProperties.getGrpcReactiveUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.intercept(logGrpcInterceptor)
			.build(ReactorArticleServiceGrpc.ReactorArticleServiceStub.class);
		return new ArticleGrpcReactiveOperation(grpcClient);
	}

	@Bean
	ArticleGrpcOperation articleGrpcArmeriaOperation(HelloworldTestProperties testProperties,
			LogGrpcInterceptor logGrpcInterceptor) {
		var grpcClient = GrpcClients.builder(testProperties.getGrpcArmeriaUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.intercept(logGrpcInterceptor)
			.build(ArticleServiceGrpc.ArticleServiceBlockingStub.class);
		return new ArticleGrpcOperation(grpcClient);
	}

	@Bean
	ArticleGrpcReactiveOperation articleGrpcArmeriaReactiveOperation(HelloworldTestProperties testProperties,
			LogGrpcInterceptor logGrpcInterceptor) {
		var grpcClient = GrpcClients.builder(testProperties.getGrpcArmeriaReactiveUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.intercept(logGrpcInterceptor)
			.build(ReactorArticleServiceGrpc.ReactorArticleServiceStub.class);
		return new ArticleGrpcReactiveOperation(grpcClient);
	}

	@Bean
	HelloGrpcOperation helloGrpcArmeriaOperation(HelloworldTestProperties testProperties,
			LogGrpcInterceptor logGrpcInterceptor) {
		var grpcClient = GrpcClients.builder(testProperties.getGrpcArmeriaUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.intercept(logGrpcInterceptor)
			.build(HelloServiceGrpc.HelloServiceBlockingStub.class);
		return new HelloGrpcOperation(grpcClient);
	}

}
