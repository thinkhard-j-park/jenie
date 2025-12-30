package org.jenie.spring.helloworld.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({ HelloworldProperties.class, HelloworldCryptoProperties.class })
@Configuration(proxyBeanMethods = false)
public class HelloworldConfig {

}
