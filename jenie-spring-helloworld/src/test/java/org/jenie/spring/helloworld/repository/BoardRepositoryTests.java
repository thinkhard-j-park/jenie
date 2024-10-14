package org.jenie.spring.helloworld.repository;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BoardRepositoryTests {

	@InjectMocks
	private BoardRepository boardRepository;

	@Mock
	private MongoTemplateRouter mongoTemplateRouter;

	@Mock
	private MongoTemplate mongoTemplate;

	@Test
	void findBoardById() {

		// given
		var service = "jenie-test";
		var id = "board-id";

		var boardEntity = new BoardEntity();
		boardEntity.setId(id);

		given(this.mongoTemplateRouter.mongoTemplate(service)).willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		given(this.mongoTemplate.findOne(any(Query.class), eq(BoardEntity.class))).willReturn(boardEntity);

		// when
		var result = this.boardRepository.findBoardById(service, id);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(service);

		verify(this.mongoTemplate).findOne(queryCaptor.capture(), eq(BoardEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(1);
		assertThat(capturedQuery.getQueryObject().get("_id")).isEqualTo(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
	}

}
