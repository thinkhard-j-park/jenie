package org.jenie.spring.helloworld.dto;

import java.util.List;

import org.jenie.spring.helloworld.entity.ArticleHeader;

public class ArticleHeaderList {

	private List<ArticleHeader> articleHeaders;

	private boolean hasMore = false;

	public List<ArticleHeader> getArticleHeaders() {
		return this.articleHeaders;
	}

	public void setArticleHeaders(List<ArticleHeader> articleHeaders) {
		this.articleHeaders = articleHeaders;
	}

	public boolean isHasMore() {
		return this.hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "ArticleHeaderList{" +
				"articleHeaders=" + this.articleHeaders +
				", hasMore=" + this.hasMore +
				'}';
		//@formatter:on
	}

}
