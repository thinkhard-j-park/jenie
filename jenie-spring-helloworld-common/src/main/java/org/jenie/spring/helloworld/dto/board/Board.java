package org.jenie.spring.helloworld.dto.board;

import com.google.common.base.Strings;

public record Board(String id, String name, String rootId, String parentId) {

	public static BoardMessage toProtoMessage(Board board) {
		if (board == null) {
			return null;
		}
		return BoardMessage.newBuilder()
			.setId(Strings.nullToEmpty(board.id()))
			.setName(Strings.nullToEmpty(board.name()))
			.setRootId(Strings.nullToEmpty(board.rootId()))
			.setParentId(Strings.nullToEmpty(board.parentId()))
			.build();
	}

	public static Board fromProtoMessage(BoardMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}
		return Board.newBuilder()
			.id(protoMessage.getId())
			.name(protoMessage.getName())
			.rootId(protoMessage.getRootId())
			.parentId(protoMessage.getParentId())
			.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String id;

		private String name;

		private String rootId;

		private String parentId;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder rootId(String rootId) {
			this.rootId = rootId;
			return this;
		}

		public Builder parentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public Board build() {
			return new Board(this.id, this.name, this.rootId, this.parentId);
		}

	}
}
