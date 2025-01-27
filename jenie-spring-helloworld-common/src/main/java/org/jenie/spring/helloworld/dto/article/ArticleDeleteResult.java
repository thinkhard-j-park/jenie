package org.jenie.spring.helloworld.dto.article;

import java.time.ZonedDateTime;

import org.jenie.spring.helloworld.utils.ZdtUtil;

public record ArticleDeleteResult(String id, int state, ZonedDateTime deletedAt) {
	public static ArticleDeleteResultMessage toProtoMessage(ArticleDeleteResult deleteResult) {
		if (deleteResult == null) {
			return null;
		}

		return ArticleDeleteResultMessage.newBuilder()
			.setId(deleteResult.id())
			.setState(deleteResult.state())
			.setDeletedAt(ZdtUtil.toTimestamp(deleteResult.deletedAt()))
			.build();
	}

	public static ArticleDeleteResult fromProtoMessage(ArticleDeleteResultMessage deleteResultMessage) {
		if (deleteResultMessage == null) {
			return null;
		}
		return new ArticleDeleteResult(deleteResultMessage.getId(), deleteResultMessage.getState(),
				ZdtUtil.fromTimestamp(deleteResultMessage.getDeletedAt()));
	}
}
