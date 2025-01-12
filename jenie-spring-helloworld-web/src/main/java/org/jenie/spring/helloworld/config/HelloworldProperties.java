package org.jenie.spring.helloworld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "helloworld")
public class HelloworldProperties {

	private boolean serverCompression;

	private boolean useBlockingTaskExecutor;

	private boolean useDocs;

	public boolean isServerCompression() {
		return this.serverCompression;
	}

	public void setServerCompression(boolean serverCompression) {
		this.serverCompression = serverCompression;
	}

	public boolean isUseBlockingTaskExecutor() {
		return this.useBlockingTaskExecutor;
	}

	public void setUseBlockingTaskExecutor(boolean useBlockingTaskExecutor) {
		this.useBlockingTaskExecutor = useBlockingTaskExecutor;
	}

	public boolean isUseDocs() {
		return this.useDocs;
	}

	public void setUseDocs(boolean useDocs) {
		this.useDocs = useDocs;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "HelloworldProperties{" +
				"serverCompression=" + this.serverCompression +
				", useBlockingTaskExecutor=" + this.useBlockingTaskExecutor +
				", useDocs=" + this.useDocs +
				'}';
		//@formatter:on
	}

}
