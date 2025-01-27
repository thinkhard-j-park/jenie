package org.jenie.spring.helloworld.common;

public class Reaction {

	private long viewCount;

	public Reaction() {
	}

	public Reaction(long viewCount) {
		this.viewCount = viewCount;
	}

	public static ReactionMessage toProtoMessage(Reaction reaction) {
		if (reaction == null) {
			return null;
		}
		return ReactionMessage.newBuilder().setViewCount(reaction.getViewCount()).build();
	}

	public static Reaction fromProtoMessage(ReactionMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}

		return new Reaction(protoMessage.getViewCount());
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
