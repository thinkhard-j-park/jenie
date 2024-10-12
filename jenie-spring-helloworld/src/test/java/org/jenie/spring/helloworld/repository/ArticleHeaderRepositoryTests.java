package org.jenie.spring.helloworld.repository;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.mongodb.ReadPreference;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleHeaderRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ArticleHeaderRepositoryTests.class);

	@InjectMocks
	private ArticleHeaderRepository articleHeaderRepository;

	@Mock
	private MongoTemplateRouter mongoTemplateRouter;

	@Mock
	private MongoTemplate mongoTemplate;

	static Stream<Arguments> provideReadPreferenceWithLatest() {
		return Stream.of(Arguments.of(true, ReadPreference.primary()),
				Arguments.of(false, ReadPreference.secondaryPreferred()));
	}

	@ParameterizedTest
	@MethodSource("provideReadPreferenceWithLatest")
	void findArticleHeaderById(boolean latest, ReadPreference expectedReadPreference) {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setBoardId(boardId);

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), isNull()))
			.willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		given(this.mongoTemplate.findOne(any(Query.class), eq(ArticleHeaderEntity.class))).willReturn(headerEntity);

		// when
		var result = this.articleHeaderRepository.findArticleHeaderById(service, id, latest);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(), isNull());
		var capturedReadPreference = readPreferenceCaptor.getValue();
		assertThat(capturedReadPreference).isNotNull();
		assertThat(capturedReadPreference.getName()).isEqualTo(expectedReadPreference.getName());

		verify(this.mongoTemplate).findOne(queryCaptor.capture(), eq(ArticleHeaderEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(1);
		assertThat(capturedQuery.getQueryObject().get("_id")).isEqualTo(id);

		assertThat(capturedQuery).isNotNull();
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getBoardId()).isEqualTo(boardId);
	}

	@Test
	void findArticleWriterById() {
		// given
		var service = "jenie-test";
		var id = "article-id";
		var writer = new Writer("wid", "name");

		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setWriter(writer);

		given(this.mongoTemplateRouter.mongoTemplate(service)).willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		given(this.mongoTemplate.findOne(any(Query.class), eq(ArticleHeaderEntity.class))).willReturn(headerEntity);

		// when
		var result = this.articleHeaderRepository.findArticleWriterById(service, id);

		// then
		verify(this.mongoTemplate).findOne(queryCaptor.capture(), eq(ArticleHeaderEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(1);
		assertThat(capturedQuery.getQueryObject().get("_id")).isEqualTo(id);
		assertThat(capturedQuery.getFieldsObject().keySet()).hasSize(1);
		assertThat(capturedQuery.getFieldsObject().containsKey("writer")).isTrue();

		assertThat(result).isNotNull();
		assertThat(result.getWid()).isEqualTo(writer.getWid());
		assertThat(result.getName()).isEqualTo(writer.getName());
	}

	static Stream<Arguments> provideListArticleHeader() {
		return Stream.of(Arguments.of("", "", 1, SortCode.TIME_DESC, -1));
	}

	@ParameterizedTest
	@MethodSource("provideListArticleHeader")
	// TODO 입력 파라미터 갯수가 많으므로, ListArticleHeaderRequestParam 과 expectedValue 을 이용한다.
	void listArticleHeader(String boardId, String prevId, int querySize, SortCode sortCode, int expectedSortOrder) {
		// given
		var service = "jenie-test";
		var title = "title-";
		var param = new ListArticleHeaderRequestParam(boardId, prevId, 5, sortCode.getCode());
		var entityList = new ArrayList<ArticleHeaderEntity>();
		for (int i = 0; i < 3; i++) {
			var headerEntity = new ArticleHeaderEntity();
			headerEntity.setId("article-id-" + i);
			headerEntity.setBoardId(boardId);
			headerEntity.setTitle(title + i);
			headerEntity.setWriter(new Writer("wid-" + i, "name-" + i));
			entityList.add(headerEntity);
		}

		given(this.mongoTemplateRouter.mongoTemplate(service)).willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		given(this.mongoTemplate.find(any(Query.class), eq(ArticleHeaderEntity.class))).willReturn(entityList);

		// when
		var result = this.articleHeaderRepository.listArticleHeader(service, param);

		// then
		verify(this.mongoTemplate).find(queryCaptor.capture(), eq(ArticleHeaderEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(querySize);

		assertThat(capturedQuery.getQueryObject().get("state")).isEqualTo(ArticleState.Normal.getCode());

		if (StringUtils.hasText(boardId)) {
			assertThat(capturedQuery.getQueryObject().get("boardId")).isEqualTo(boardId);
		}

		if (StringUtils.hasText(prevId)) {
			assertThat(capturedQuery.getQueryObject().get("_id")).isEqualTo(prevId);
		}

		assertThat(capturedQuery.getSortObject().get(SortOrder.fromCode(sortCode.getCode()).getField()))
			.isEqualTo(expectedSortOrder);
		assertThat(capturedQuery.getLimit()).isEqualTo(param.size() + 1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(3);
		assertThat(result).allSatisfy((headerEntity) -> {
			assertThat(headerEntity.getId()).isNotEmpty();
			assertThat(headerEntity.getBoardId()).isEqualTo(boardId);
			assertThat(headerEntity.getTitle()).isNotEmpty();
			assertThat(headerEntity.getWriter()).isNotNull();
			assertThat(headerEntity.getWriter().getWid()).isNotEmpty();
		});

	}

}
