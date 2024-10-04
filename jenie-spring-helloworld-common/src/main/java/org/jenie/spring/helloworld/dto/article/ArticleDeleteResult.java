package org.jenie.spring.helloworld.dto.article;

import java.time.ZonedDateTime;

public record ArticleDeleteResult(String id, int state, ZonedDateTime deletedAt) {
}
