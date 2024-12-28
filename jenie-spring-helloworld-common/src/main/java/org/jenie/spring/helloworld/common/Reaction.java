package org.jenie.spring.helloworld.common;

import org.jenie.spring.helloworld.grpc.ReactionMessage;

public class Reaction {

	private long viewCount;

	public static ReactionMessage toProtoMessage(Reaction reaction) {
		if (reaction == null) {
			return null;
		}
		return ReactionMessage.newBuilder().setViewCount(reaction.getViewCount()).build();
	}

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
