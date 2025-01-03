package org.jenie.spring.helloworld.service;

import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BoardServiceTests {

	@InjectMocks
	private BoardService boardService;

	@Mock
	private BoardRepository boardRepository;

	@Test
	void findBoardEntityById() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);

		given(this.boardRepository.findBoardById(service, boardId)).willReturn(boardEntity);

		// when
		var result = this.boardService.findBoardEntityById(service, boardId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNotEmpty();
	}

}
