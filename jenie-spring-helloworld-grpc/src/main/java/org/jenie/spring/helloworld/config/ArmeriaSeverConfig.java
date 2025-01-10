package org.jenie.spring.helloworld.config;

import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.jenie.spring.helloworld.exception.GrpcExceptionHandler;
import org.jenie.spring.helloworld.grpc.ArticleGrpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArmeriaSeverConfig {

	private final ArticleGrpc articlesGrpc;

	private final GrpcExceptionHandler grpcExceptionHandler;

	public ArmeriaSeverConfig(ArticleGrpc articleGrpc, GrpcExceptionHandler grpcExceptionHandler) {
		this.articlesGrpc = articleGrpc;
		this.grpcExceptionHandler = grpcExceptionHandler;
	}

	@Bean
	public ArmeriaServerConfigurator armeriaSeverConfigConfigurator() {
		return (serverBuilder) -> {
			var grpcBuilder = GrpcService.builder()
				.addService(this.articlesGrpc)
				.exceptionHandler(this.grpcExceptionHandler)
				.enableUnframedRequests(true)
				.autoCompression(true)
				.build();
			serverBuilder.service(grpcBuilder, LoggingService.newDecorator());
		};
	}

}
