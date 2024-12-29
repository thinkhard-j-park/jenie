package org.jenie.spring.helloworld.dto.article;

import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Reaction;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.board.Board;

public record ArticleHeader(String id, Board board, int state, String title, Reaction reaction, Writer writer,
		ActionDateTime actionDateTime) {

	public static ArticleHeaderMessage toProtoMessage(ArticleHeader articleHeader) {
		if (articleHeader == null) {
			return null;
		}

		var builder = ArticleHeaderMessage.newBuilder();
		builder.setId(articleHeader.id()).setState(articleHeader.state()).setTitle(articleHeader.title());

		if (articleHeader.board() != null) {
			builder.setBoard(Board.toProtoMessage(articleHeader.board()));
		}

		if (articleHeader.reaction() != null) {
			builder.setReaction(Reaction.toProtoMessage(articleHeader.reaction()));
		}

		if (articleHeader.writer() != null) {
			builder.setWriter(Writer.toProtoMessage(articleHeader.writer()));
		}

		if (articleHeader.actionDateTime() != null) {
			builder.setActionDateTime(ActionDateTime.toProtoMessage(articleHeader.actionDateTime()));
		}

		return builder.build();
	}

	public static ArticleHeader fromProtoMessage(ArticleHeaderMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}

		return ArticleHeader.newBuilder()
			.id(protoMessage.getId())
			.board(Board.fromProtoMessage(protoMessage.getBoard()))
			.state(ArticleState.fromCode(protoMessage.getState()))
			.title(protoMessage.getTitle())
			.reaction(Reaction.fromProtoMessage(protoMessage.getReaction()))
			.writer(Writer.fromProtoMessage(protoMessage.getWriter()))
			.actionDateTime(ActionDateTime.fromProtoMessage(protoMessage.getActionDateTime()))
			.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String id;

		private Board board;

		private ArticleState state = ArticleState.Normal;

		private String title;

		private Reaction reaction;

		private Writer writer;

		private ActionDateTime actionDateTime;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder board(Board board) {
			this.board = board;
			return this;
		}

		public Builder state(ArticleState state) {
			this.state = state;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder reaction(Reaction reaction) {
			this.reaction = reaction;
			return this;
		}

		public Builder writer(Writer writer) {
			this.writer = writer;
			return this;
		}

		public Builder actionDateTime(ActionDateTime actionDateTime) {
			this.actionDateTime = actionDateTime;
			return this;
		}

		public ArticleHeader build() {
			return new ArticleHeader(this.id, this.board, this.state.getCode(), this.title, this.reaction, this.writer,
					this.actionDateTime);
		}

	}
}
