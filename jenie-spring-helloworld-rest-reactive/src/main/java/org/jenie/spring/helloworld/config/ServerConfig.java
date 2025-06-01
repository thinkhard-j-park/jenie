package org.jenie.spring.helloworld.config;

import org.jenie.spring.helloworld.log.access.ReactorNettyAccessLogCustomizer;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;

@Configuration
public class ServerConfig implements ReactorNettyAccessLogCustomizer {

	@Bean
	public NettyReactiveWebServerFactory nettyWebServerCustomizer(ReactorResourceFactory resourceFactory) {
		NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
		serverFactory.setResourceFactory(resourceFactory);
		serverFactory.addServerCustomizers(accessLogCustomizer());
		return serverFactory;

	}

}
