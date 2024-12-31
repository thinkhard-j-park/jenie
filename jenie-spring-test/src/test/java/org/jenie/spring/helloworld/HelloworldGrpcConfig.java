package org.jenie.spring.helloworld;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.jenie.spring.client.LogGrpcInterceptor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ GrpcClientAutoConfiguration.class })
@EnableConfigurationProperties(HelloworldTestProperties.class)
@Configuration
public class HelloworldGrpcConfig {

	@Bean
	LogGrpcInterceptor logGrpcInterceptor(HelloworldTestProperties testProperties) {
		return new LogGrpcInterceptor(testProperties);
	}

}
