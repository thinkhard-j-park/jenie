package org.jenie.spring.helloworld.config;

import org.jenie.spring.helloworld.log.access.ReactorNettyAccessLogCustomizer;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;

@Configuration
public class ServerConfig implements ReactorNettyAccessLogCustomizer {

	@Bean
	public NettyReactiveWebServerFactory nettyWebServerCustomizer(ReactorResourceFactory resourceFactory,
			HelloworldProperties properties) {
		NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
		serverFactory.setResourceFactory(resourceFactory);

		if (properties.isEnableAccessLog()) {
			serverFactory.addServerCustomizers(accessLogCustomizer());
		}

		return serverFactory;

	}

}
