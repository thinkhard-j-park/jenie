package org.jenie.spring.helloworld.dto.article;

import org.jenie.spring.helloworld.pojo.Writer;

public record ArticleRequest(String boardId, String title, String content, Writer writer) {

}
