package org.jenie.spring.helloworld.dto.article;

import com.google.common.base.Strings;

public record Article(ArticleHeader header, String content) {
	public static Article fromProtoMessage(ArticleMessage articleMessage) {
		if (articleMessage == null) {
			return null;
		}

		ArticleHeader header = ArticleHeader.fromProtoMessage(articleMessage.getHeader());
		return new Article(header, articleMessage.getContent());
	}

	public static ArticleMessage toProtoMessage(Article article) {
		if (article == null) {
			return null;
		}
		var builder = ArticleMessage.newBuilder().setContent(Strings.nullToEmpty(article.content()));

		if (article.header() != null) {
			builder.setHeader(ArticleHeader.toProtoMessage(article.header()));
		}

		return builder.build();
	}
}
