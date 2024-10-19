package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

public final class BoardMapper {

	@ExcludeCodeCoverageGenerated
	private BoardMapper() {
	}

	public static Board toDto(BoardEntity board) {
		if (board == null) {
			return null;
		}
		return Board.builder()
			.id(board.getId())
			.rootId(board.getRootId())
			.parentId(board.getParentId())
			.name(board.getName())
			.build();
	}

}
