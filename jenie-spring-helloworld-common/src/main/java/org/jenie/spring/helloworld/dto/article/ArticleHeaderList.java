package org.jenie.spring.helloworld.dto.article;

import java.util.Collections;
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

	public static ArticleHeaderListMessage toProtoMessage(ArticleHeaderList articleHeaderList) {
		if (articleHeaderList == null) {
			return null;
		}

		var builder = ArticleHeaderListMessage.newBuilder().setHasMore(articleHeaderList.hasMore());
		if (!ObjectUtils.isEmpty(articleHeaderList.list())) {
			builder.addAllList(articleHeaderList.list().stream().map(ArticleHeader::toProtoMessage).toList());
		}
		return builder.build();
	}

	public static ArticleHeaderList fromProtoMessage(ArticleHeaderListMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}

		if (protoMessage.getListCount() == 0) {
			return new ArticleHeaderList(Collections.emptyList(), protoMessage.getHasMore());
		}

		return new ArticleHeaderList(protoMessage.getListList().stream().map(ArticleHeader::fromProtoMessage).toList(),
				protoMessage.getHasMore());
	}
}
