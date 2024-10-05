package org.jenie.spring.helloworld.common;

public class Reaction {

	private long viewCount;

	public long getViewCount() {
		return this.viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "Reaction{" +
				"viewCount=" + this.viewCount +
				'}';
		//@formatter:on
	}

}
