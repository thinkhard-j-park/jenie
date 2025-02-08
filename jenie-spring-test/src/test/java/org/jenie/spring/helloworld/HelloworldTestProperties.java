package org.jenie.spring.helloworld;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "helloworld")
public class HelloworldTestProperties {

	private String clientName;

	private String restUrl;

	private String restReactiveUrl;

	private String grpcUrl;

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getRestUrl() {
		return this.restUrl;
	}

	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}

	public String getGrpcUrl() {
		return this.grpcUrl;
	}

	public void setGrpcUrl(String grpcUrl) {
		this.grpcUrl = grpcUrl;
	}

	public String getRestReactiveUrl() {
		return this.restReactiveUrl;
	}

	public void setRestReactiveUrl(String restReactiveUrl) {
		this.restReactiveUrl = restReactiveUrl;
	}

}
