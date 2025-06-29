package org.jenie.spring.helloworld.config;

import io.grpc.ServerInterceptor;
import org.jenie.spring.helloworld.log.access.SpringGrpcAccessLogInterceptor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration
public class ServerConfig {

	@ConditionalOnProperty(name = { "helloworld.enable-access-log" }, havingValue = "true")
	@Configuration
	public static class AccessLogConfig {

		@Bean
		@Order(100)
		@GlobalServerInterceptor
		ServerInterceptor accessLogInterceptor() {
			return new SpringGrpcAccessLogInterceptor();
		}

	}

}
