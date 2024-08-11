package org.jenie.spring.helloworld.dto.article;

import java.util.List;

import org.springframework.util.ObjectUtils;

public record ArticleHeaderList(List<ArticleHeader> list, boolean hasMore) {
	public static ArticleHeaderList from(List<ArticleHeader> list, int size) {
		var hasMore = false;
		if (!ObjectUtils.isEmpty(list) && list.size() > size) {
			list = list.subList(0, size);
			hasMore = true;
		}

		return new ArticleHeaderList(list, hasMore);
	}
}
