package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Reaction;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleHeaderMapperTests {

	@Test
	void toDto() {
		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId("article-header-id");
		articleHeaderEntity.setBoardId("board-id");
		articleHeaderEntity.setState(ArticleState.Normal.getCode());
		articleHeaderEntity.setTitle("title");
		articleHeaderEntity.setReaction(new Reaction());
		articleHeaderEntity.setWriter(new Writer("writer-id", "writer-name"));
		articleHeaderEntity.setActionDateTime(new ActionDateTime());

		var articleHeader = ArticleHeaderMapper.toDto(articleHeaderEntity);
		assertThat(articleHeader).isNotNull();
		assertThat(articleHeader.id()).isEqualTo(articleHeaderEntity.getId());
		assertThat(articleHeader.board()).isNotNull();
		assertThat(articleHeader.board().id()).isEqualTo(articleHeaderEntity.getBoardId());
		assertThat(articleHeader.state()).isEqualTo(articleHeaderEntity.getState());
		assertThat(articleHeader.title()).isEqualTo(articleHeaderEntity.getTitle());
		assertThat(articleHeader.reaction()).isEqualTo(articleHeaderEntity.getReaction());
		assertThat(articleHeader.writer()).isEqualTo(articleHeaderEntity.getWriter());
		assertThat(articleHeader.actionDateTime()).isEqualTo(articleHeaderEntity.getActionDateTime());
	}

	@Test
	void toDtoWithBoard() {
		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId("article-header-id");
		articleHeaderEntity.setBoardId("board-id");
		articleHeaderEntity.setState(ArticleState.Normal.getCode());
		articleHeaderEntity.setTitle("title");
		articleHeaderEntity.setReaction(new Reaction());
		articleHeaderEntity.setWriter(new Writer("writer-id", "writer-name"));
		articleHeaderEntity.setActionDateTime(new ActionDateTime());

		var boardEntity = new BoardEntity();
		boardEntity.setId("board-id");
		boardEntity.setName("board-name");
		boardEntity.setParentId("parent-id");
		boardEntity.setRootId("root-id");

		var articleHeader = ArticleHeaderMapper.toDto(articleHeaderEntity, boardEntity);
		assertThat(articleHeader).isNotNull();
		assertThat(articleHeader.id()).isEqualTo(articleHeaderEntity.getId());
		assertThat(articleHeader.board()).isNotNull();
		assertThat(articleHeader.board().id()).isEqualTo(boardEntity.getId());
		assertThat(articleHeader.board().name()).isEqualTo(boardEntity.getName());
		assertThat(articleHeader.board().parentId()).isEqualTo(boardEntity.getParentId());
		assertThat(articleHeader.board().rootId()).isEqualTo(boardEntity.getRootId());
		assertThat(articleHeader.state()).isEqualTo(articleHeaderEntity.getState());
		assertThat(articleHeader.title()).isEqualTo(articleHeaderEntity.getTitle());
		assertThat(articleHeader.reaction()).isEqualTo(articleHeaderEntity.getReaction());
		assertThat(articleHeader.writer()).isEqualTo(articleHeaderEntity.getWriter());
		assertThat(articleHeader.actionDateTime()).isEqualTo(articleHeaderEntity.getActionDateTime());
	}

	@Test
	void toDtoNull() {
		assertThat(ArticleHeaderMapper.toDto(null)).isNull();
	}

}
