package org.jenie.spring.helloworld.dto.article;

import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.pojo.ActionDateTime;
import org.jenie.spring.helloworld.pojo.ArticleState;
import org.jenie.spring.helloworld.pojo.Reaction;
import org.jenie.spring.helloworld.pojo.Writer;

public record ArticleHeader(String id, Board board, int state, String title, Reaction reaction, Writer writer,
		ActionDateTime actionDateTime) {

	public static Builder builder() {
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
