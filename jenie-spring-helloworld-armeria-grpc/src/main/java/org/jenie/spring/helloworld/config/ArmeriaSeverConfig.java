package org.jenie.spring.helloworld.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.linecorp.armeria.common.util.BlockingTaskExecutor;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.jenie.spring.helloworld.exception.GrpcExceptionHandler;
import org.jenie.spring.helloworld.grpc.ArticleGrpc;
import org.jenie.spring.helloworld.grpc.HelloGrpc;
import org.jenie.spring.helloworld.log.access.ArmeriaAccessLogCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ArmeriaSeverConfig implements ArmeriaAccessLogCustomizer {

	private static final Logger logger = LoggerFactory.getLogger(ArmeriaSeverConfig.class);

	private final ArticleGrpc articlesGrpc;

	private final HelloGrpc healthGrpc;

	private final GrpcExceptionHandler grpcExceptionHandler;

	private final HelloworldProperties helloworldProperties;

	public ArmeriaSeverConfig(ArticleGrpc articleGrpc, HelloGrpc helloGrpc, GrpcExceptionHandler grpcExceptionHandler,
			HelloworldProperties helloworldProperties) {
		this.articlesGrpc = articleGrpc;
		this.healthGrpc = helloGrpc;
		this.grpcExceptionHandler = grpcExceptionHandler;
		this.helloworldProperties = helloworldProperties;
	}

	@Bean
	public ArmeriaServerConfigurator armeriaSeverConfigConfigurator() {
		return (serverBuilder) -> {
			logger.info("Custom properties: {}", this.helloworldProperties);

			var grpcService = GrpcService.builder()
				.addService(this.articlesGrpc)
				.addService(this.healthGrpc)
				.exceptionHandler(this.grpcExceptionHandler)
				.enableHealthCheckService(true)
				.useBlockingTaskExecutor(this.helloworldProperties.isUseBlockingTaskExecutor())
				.autoCompression(this.helloworldProperties.isServerCompression())
				.enableUnframedRequests(this.helloworldProperties.isUseDocs())
				.enableHttpJsonTranscoding(this.helloworldProperties.isUseGrpcJsonTranscoder())
				.build();
			serverBuilder.service(grpcService, LoggingService.newDecorator());

			if (this.helloworldProperties.isUseDocs()) {
				serverBuilder.serviceUnder("/docs", new DocService());
			}
			if (this.helloworldProperties.isEnableAccessLog()) {
				serverBuilder.accessLogWriter(accessLogWriter(), true);
			}
		};
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(name = { "helloworld.use-blocking-task-executor", "helloworld.use-virtual-thread" },
			havingValue = "true")
	public static class VirtualThreadConfig {

		@Bean(name = "virtualThreadExecutor", destroyMethod = "shutdown")
		public ScheduledExecutorService virtualThreadExecutor() {
			return new ScheduledThreadPoolExecutor(0, Thread.ofVirtual().factory());
		}

		@Bean
		public BlockingTaskExecutor blockingTaskExecutor(
				@Qualifier("virtualThreadExecutor") ScheduledExecutorService virtualThreadExecutor) {
			logger.info("Using virtual thread blocking executor");
			return BlockingTaskExecutor.of(virtualThreadExecutor);
		}

	}

}
