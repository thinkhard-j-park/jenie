package org.jenie.spring.test.data.mongodb.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb.setting")
public class MongoDBSetting {

	private boolean enabled = false;

	private String appName;

	private Map<String, MongoDBCluster> cluster = new HashMap<>();

	public Map<String, MongoDBCluster> getCluster() {
		return this.cluster;
	}

	public void setCluster(Map<String, MongoDBCluster> cluster) {
		this.cluster = cluster;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
