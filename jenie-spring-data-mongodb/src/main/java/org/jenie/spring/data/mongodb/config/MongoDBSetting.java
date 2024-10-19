package org.jenie.spring.data.mongodb.config;

import java.util.HashMap;
import java.util.Map;

import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ExcludeCodeCoverageGenerated
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

	@ExcludeCodeCoverageGenerated
	@Override
	public String toString() {
		//@formatter:off
		return "MongoDBSetting{" +
				"appName='" + this.appName + '\'' +
				", enabled=" + this.enabled +
				", cluster=" + this.cluster +
				'}';
		//@formatter:on
	}

}
