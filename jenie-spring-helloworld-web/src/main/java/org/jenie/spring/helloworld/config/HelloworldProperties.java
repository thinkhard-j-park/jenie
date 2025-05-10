package org.jenie.spring.helloworld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "helloworld")
public class HelloworldProperties {

	private boolean serverCompression;

	private boolean useBlockingTaskExecutor;

	private boolean useDocs;

	private boolean useVirtualThread;

	private boolean useGrpcJsonTranscoder;

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

	public boolean isUseVirtualThread() {
		return this.useVirtualThread;
	}

	public void setUseVirtualThread(boolean useVirtualThread) {
		this.useVirtualThread = useVirtualThread;
	}

	public boolean isUseGrpcJsonTranscoder() {
		return this.useGrpcJsonTranscoder;
	}

	public void setUseGrpcJsonTranscoder(boolean useGrpcJsonTranscoder) {
		this.useGrpcJsonTranscoder = useGrpcJsonTranscoder;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "HelloworldProperties{" +
				"serverCompression=" + this.serverCompression +
				", useBlockingTaskExecutor=" + this.useBlockingTaskExecutor +
				", useDocs=" + this.useDocs +
				", useVirtualThread=" + this.useVirtualThread +
				", useGrpcJsonTranscoder=" + this.useGrpcJsonTranscoder +
				'}';
		//@formatter:on
	}

}
