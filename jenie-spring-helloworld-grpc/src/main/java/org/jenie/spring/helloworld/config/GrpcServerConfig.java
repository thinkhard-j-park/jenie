package org.jenie.spring.helloworld.config;

import java.util.concurrent.Executors;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {

	private static final Logger logger = LoggerFactory.getLogger(GrpcServerConfig.class);

	@Bean
	@ConditionalOnProperty(name = "helloworld.use-virtual-thread", havingValue = "true")
	public GrpcServerConfigurer virtualThreadConfig() {
		return (serverBuilder) -> {
			if (serverBuilder instanceof NettyServerBuilder nettyServerBuilder) {
				logger.info("Virtual Thread enabled");
				nettyServerBuilder.executor(Executors.newVirtualThreadPerTaskExecutor());
			}
		};
	}

}
