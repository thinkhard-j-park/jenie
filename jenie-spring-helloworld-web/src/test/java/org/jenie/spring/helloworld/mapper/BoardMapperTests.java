package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardMapperTests {

	@Test
	void toDto() {
		var boardEntity = new BoardEntity();
		boardEntity.setId("board-id");
		boardEntity.setName("board-name");
		boardEntity.setParentId("parent-id");
		boardEntity.setRootId("root-id");

		var board = BoardMapper.toDto(boardEntity);
		assertThat(board).isNotNull();
		assertThat(board.id()).isEqualTo(boardEntity.getId());
		assertThat(board.name()).isEqualTo(boardEntity.getName());
		assertThat(board.parentId()).isEqualTo(boardEntity.getParentId());
		assertThat(board.rootId()).isEqualTo(boardEntity.getRootId());
	}

	@Test
	void toDtoWithNull() {
		assertThat(BoardMapper.toDto(null)).isNull();
	}

}
