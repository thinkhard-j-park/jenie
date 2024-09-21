package org.jenie.spring.test.helloworld.entity.article;

import org.jenie.spring.helloworld.pojo.ActionDateTime;
import org.jenie.spring.helloworld.pojo.Reaction;
import org.jenie.spring.helloworld.pojo.Writer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "article-header")
public class ArticleHeaderEntity {

	@Id
	private String id;

	private String boardId;

	private String title;

	private Reaction reaction;

	private Writer writer;

	private ActionDateTime actionDateTime;

	public String getBoardId() {
		return this.boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Reaction getReaction() {
		return this.reaction;
	}

	public void setReaction(Reaction reaction) {
		this.reaction = reaction;
	}

	public Writer getWriter() {
		return this.writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public ActionDateTime getActionDateTime() {
		return this.actionDateTime;
	}

	public void setActionDateTime(ActionDateTime actionDateTime) {
		this.actionDateTime = actionDateTime;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "ArticleHeaderEntity{" +
				"id='" + this.id + '\'' +
				", boardId='" + this.boardId + '\'' +
				", title='" + this.title + '\'' +
				", reaction=" + this.reaction +
				", writer=" + this.writer +
				", actionDateTime=" + this.actionDateTime +
				'}';
		//@formatter:on
	}

}
