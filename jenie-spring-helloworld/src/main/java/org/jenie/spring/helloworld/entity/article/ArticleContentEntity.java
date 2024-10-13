package org.jenie.spring.helloworld.entity.article;

import org.jenie.spring.helloworld.common.ExcludeFromCodeCoverage;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "article-content")
public class ArticleContentEntity {

	@Id
	private String id;

	private String content;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ExcludeFromCodeCoverage
	@Override
	public String toString() {
		//@formatter:off
		return "ArticleContentEntity{" +
				"id='" + this.id + '\'' +
				", content='" + this.content + '\'' +
				'}';
		//@formatter:on
	}

}
