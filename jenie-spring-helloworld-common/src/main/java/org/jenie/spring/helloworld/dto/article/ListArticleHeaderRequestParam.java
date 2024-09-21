package org.jenie.spring.helloworld.dto.article;

public record ListArticleHeaderRequestParam(String boardId, String prevArticleId, int size, int sort) {
}
