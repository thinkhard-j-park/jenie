package org.jenie.spring.helloworld.config;

import java.util.concurrent.Executors;

import com.linecorp.armeria.common.util.BlockingTaskExecutor;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.jenie.spring.helloworld.exception.GrpcExceptionHandler;
import org.jenie.spring.helloworld.grpc.ArticleGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArmeriaSeverConfig {

	private static final Logger logger = LoggerFactory.getLogger(ArmeriaSeverConfig.class);

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
	public ArmeriaServerConfigurator armeriaSeverConfigConfigurator() {
		return (serverBuilder) -> {
			logger.info("Custom properties: {}", this.helloworldProperties);

			var grpcService = GrpcService.builder()
				.addService(this.articlesGrpc)
				.exceptionHandler(this.grpcExceptionHandler)
				.enableHealthCheckService(true)
				.useBlockingTaskExecutor(this.helloworldProperties.isUseBlockingTaskExecutor())
				.autoCompression(this.helloworldProperties.isServerCompression())
				.enableUnframedRequests(this.helloworldProperties.isUseDocs())
				.build();
			serverBuilder.service(grpcService, LoggingService.newDecorator());

			if (this.helloworldProperties.isUseVirtualThread()) {
				var virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
				var blockingExecutor = BlockingTaskExecutor.builder()
					.taskFunction((runnable) -> () -> virtualThreadExecutor.submit(runnable))
					.build();
				serverBuilder.blockingTaskExecutor(blockingExecutor, true);
			}

			if (this.helloworldProperties.isUseDocs()) {
				serverBuilder.serviceUnder("/docs", new DocService());
			}
		};
	}

}
