package org.jenie.spring.helloworld.dto.article;

import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.pojo.ActionDateTime;
import org.jenie.spring.helloworld.pojo.Reaction;
import org.jenie.spring.helloworld.pojo.Writer;

public record ArticleHeader(String id, Board board, String title, Reaction reaction, Writer writer,
		ActionDateTime actionDateTime) {
}
