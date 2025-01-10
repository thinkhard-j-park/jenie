package org.jenie.spring.helloworld.config;

import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.jenie.spring.helloworld.exception.GrpcExceptionHandler;
import org.jenie.spring.helloworld.grpc.ArticleGrpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ArmeriaSeverConfig {

	private final ArticleGrpc articlesGrpc;

	private final GrpcExceptionHandler grpcExceptionHandler;

	private final HelloworldProperties helloworldProperties;

	public ArmeriaSeverConfig(ArticleGrpc articleGrpc, GrpcExceptionHandler grpcExceptionHandler,
			HelloworldProperties helloworldProperties) {
		this.articlesGrpc = articleGrpc;
		this.grpcExceptionHandler = grpcExceptionHandler;
		this.helloworldProperties = helloworldProperties;
	}

	@Bean
	@Profile("dev")
	public ArmeriaServerConfigurator armeriaSeverConfigConfigurator() {
		return (serverBuilder) -> {
			var grpcBuilder = GrpcService.builder()
				.addService(this.articlesGrpc)
				.exceptionHandler(this.grpcExceptionHandler)
				.enableHealthCheckService(true)
				.autoCompression(this.helloworldProperties.isServerCompression())
				.build();
			serverBuilder.service(grpcBuilder, LoggingService.newDecorator());
		};
	}

	@Bean
	@Profile("local")
	public ArmeriaServerConfigurator armeriaSeverConfigLocal() {
		return (serverBuilder) -> {
			var grpcBuilder = GrpcService.builder()
				.addService(this.articlesGrpc)
				.exceptionHandler(this.grpcExceptionHandler)
				.enableUnframedRequests(true)
				.enableHealthCheckService(true)
				.autoCompression(true)
				.build();
			serverBuilder.service(grpcBuilder, LoggingService.newDecorator());
			serverBuilder.serviceUnder("/docs", new DocService());
		};
	}

}
