package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.entity.board.BoardEntity;

public final class ArticleHeaderMapper {
	private ArticleHeaderMapper() {
	}

	public static ArticleHeader toDto(ArticleHeaderEntity entity) {
		if (entity == null) {
			return null;
		}

		return ArticleHeader.builder()
			.id(entity.getId())
			.board(Board.builder().id(entity.getBoardId()).build())
			.state(ArticleState.fromCode(entity.getState()))
			.title(entity.getTitle())
			.reaction(entity.getReaction())
			.writer(entity.getWriter())
			.actionDateTime(entity.getActionDateTime())
			.build();
	}

	public static ArticleHeader toDto(ArticleHeaderEntity article, BoardEntity board) {
		return ArticleHeader.builder()
			.id(article.getId())
			.board(BoardMapper.toDto(board))
			.state(ArticleState.fromCode(article.getState()))
			.title(article.getTitle())
			.reaction(article.getReaction())
			.writer(article.getWriter())
			.actionDateTime(article.getActionDateTime())
			.build();
	}

}
