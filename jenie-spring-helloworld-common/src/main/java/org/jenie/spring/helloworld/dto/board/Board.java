package org.jenie.spring.helloworld.dto.board;

public record Board(String id, String name, String rootId, String parentId) {

	public static Builder builder() {
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
