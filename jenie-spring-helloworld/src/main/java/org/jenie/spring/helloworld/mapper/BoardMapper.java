package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.entity.board.BoardEntity;

public final class BoardMapper {

	private BoardMapper() {
	}

	public static Board toDto(BoardEntity board) {
		return Board.builder()
			.id(board.getId())
			.rootId(board.getRootId())
			.parentId(board.getParentId())
			.name(board.getName())
			.build();
	}

}
