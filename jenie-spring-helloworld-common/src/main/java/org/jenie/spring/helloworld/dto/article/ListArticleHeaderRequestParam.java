package org.jenie.spring.helloworld.dto.article;

import org.jenie.spring.helloworld.dto.SortCode;

public class ListArticleHeaderRequestParam {

	private String boardId;

	private String prevArticleId;

	private int size = 15;

	private int sort = SortCode.TIME_DESC.getCode();

	public ListArticleHeaderRequestParam() {
	}

	public ListArticleHeaderRequestParam(String boardId, String prevArticleId, int size, int sort) {
		this.boardId = boardId;
		this.prevArticleId = prevArticleId;
		this.size = size;
		this.sort = sort;
	}

	public String getBoardId() {
		return this.boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getPrevArticleId() {
		return this.prevArticleId;
	}

	public void setPrevArticleId(String prevArticleId) {
		this.prevArticleId = prevArticleId;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSort() {
		return this.sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "ListArticleHeaderRequestParam{" +
				"boardId='" + this.boardId + '\'' +
				", prevArticleId='" + this.prevArticleId + '\'' +
				", size=" + this.size +
				", sort=" + this.sort +
				'}';
		//@formatter:on
	}

}
