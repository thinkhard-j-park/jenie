package org.jenie.spring.helloworld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "helloworld")
public class HelloworldProperties {

	private boolean serverCompression;

	public boolean isServerCompression() {
		return this.serverCompression;
	}

	public void setServerCompression(boolean serverCompression) {
		this.serverCompression = serverCompression;
	}

}
