package org.jenie.spring.helloworld.repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.bson.Document;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Reaction;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.CommonErrors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleHeaderRepositoryTests {

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
	void findArticleHeaderByIdShouldFail() {
		var service = "jenie-test";
		var id = "";
		assertThatThrownBy(() -> this.articleHeaderRepository.findArticleHeaderById(service, id, false))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
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
		verify(this.mongoTemplateRouter).mongoTemplate(service);
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

	@Test
	void findArticleWriterByIdShouldFail() {
		// given
		var service = "jenie-test";
		var id = "";

		// when, then
		assertThatThrownBy(() -> this.articleHeaderRepository.findArticleWriterById(service, id))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	static Stream<Arguments> provideListArticleHeader() {
		var param1 = new ListArticleHeaderRequestParam("", "", 5, SortCode.TIME_DESC.getCode());
		var param2 = new ListArticleHeaderRequestParam("board-id", "", 5, SortCode.TIME_DESC.getCode());
		var param3 = new ListArticleHeaderRequestParam("board-id", "prev-id", 5, SortCode.TIME_DESC.getCode());
		var param4 = new ListArticleHeaderRequestParam("board-id", "prev-id", 5, SortCode.TIME_ASC.getCode());
		var param5 = new ListArticleHeaderRequestParam("", "prev-id", 5, SortCode.TIME_ASC.getCode());

		//@formatter:off
		return Stream.of(
				Arguments.of(param1, 1, -1),
				Arguments.of(param2, 2, -1),
				Arguments.of(param3, 3, -1),
				Arguments.of(param4, 3, 1),
				Arguments.of(param5, 2, 1));
		//@formatter:on
	}

	@ParameterizedTest
	@MethodSource("provideListArticleHeader")
	void listArticleHeader(ListArticleHeaderRequestParam param, int querySize, int expectedSortOrder) {
		// given
		var service = "jenie-test";
		var title = "title-";
		var entityList = new ArrayList<ArticleHeaderEntity>();
		for (int i = 0; i < 3; i++) {
			var headerEntity = new ArticleHeaderEntity();
			headerEntity.setId("article-id-" + i);
			headerEntity.setBoardId(param.boardId());
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
		verify(this.mongoTemplateRouter).mongoTemplate(service);
		verify(this.mongoTemplate).find(queryCaptor.capture(), eq(ArticleHeaderEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(querySize);

		assertThat(capturedQuery.getQueryObject().get("state")).isEqualTo(ArticleState.Normal.getCode());

		if (StringUtils.hasText(param.boardId())) {
			assertThat(capturedQuery.getQueryObject().get("boardId")).isEqualTo(param.boardId());
		}

		if (StringUtils.hasText(param.prevArticleId())) {
			Document doc = (Document) capturedQuery.getQueryObject().get("_id");
			assertThat(doc).isNotNull();

			if (SortCode.fromCode(param.sort()) == SortCode.TIME_DESC) {
				assertThat(doc.get("$lt")).isEqualTo(param.prevArticleId());
			}
			else {
				assertThat(doc.get("$gt")).isEqualTo(param.prevArticleId());
			}
		}

		assertThat(capturedQuery.getSortObject().get(SortOrder.fromCode(param.sort()).getField()))
			.isEqualTo(expectedSortOrder);
		assertThat(capturedQuery.getLimit()).isEqualTo(param.size() + 1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(3);
		assertThat(result).allSatisfy((headerEntity) -> {
			assertThat(headerEntity.getId()).isNotEmpty();
			assertThat(headerEntity.getBoardId()).isEqualTo(param.boardId());
			assertThat(headerEntity.getTitle()).isNotEmpty();
			assertThat(headerEntity.getWriter()).isNotNull();
			assertThat(headerEntity.getWriter().getWid()).isNotEmpty();
		});
	}

	@Test
	void insert() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");

		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setTitle(title);
		articleHeaderEntity.setBoardId(boardId);
		articleHeaderEntity.setWriter(writer);

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);
		given(this.mongoTemplate.insert(articleHeaderEntity)).willReturn(articleHeaderEntity);

		// when
		var createdHeaderEntity = this.articleHeaderRepository.insert(service, articleHeaderEntity);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		verify(this.mongoTemplate).insert(any(ArticleHeaderEntity.class));
		assertThat(createdHeaderEntity).isNotNull();
		assertThat(createdHeaderEntity.getId()).isEqualTo(id);
		assertThat(createdHeaderEntity.getBoardId()).isEqualTo(boardId);
		assertThat(createdHeaderEntity.getTitle()).isEqualTo(title);
		assertThat(createdHeaderEntity.getWriter()).isNotNull();
		assertThat(createdHeaderEntity.getWriter().getWid()).isEqualTo(writer.getWid());
		assertThat(createdHeaderEntity.getActionDateTime()).isNotNull();
		assertThat(createdHeaderEntity.getActionDateTime().getCreatedAt()).isNotNull();
	}

	static Stream<Arguments> provideInvalidInsertHeaderEntity() {
		var entity1 = new ArticleHeaderEntity();

		var entity2 = new ArticleHeaderEntity();
		entity2.setBoardId("board-id");

		var entity3 = new ArticleHeaderEntity();
		entity3.setBoardId("board-id");
		entity3.setTitle("hello world");

		var entity4 = new ArticleHeaderEntity();
		entity4.setBoardId("board-id");
		entity4.setTitle("hello world");
		entity4.setWriter(new Writer("", ""));

		var entity5 = new ArticleHeaderEntity();
		entity5.setBoardId("board-id");
		entity5.setTitle("hello world");
		entity5.setWriter(new Writer("wid", ""));

		var entity6 = new ArticleHeaderEntity();
		entity6.setBoardId("board-id");
		entity6.setTitle("hello world");
		entity6.setWriter(new Writer("", "wid"));

		//@formatter:off
		return Stream.of(
				Arguments.of(entity1),
				Arguments.of(entity2),
				Arguments.of(entity3),
				Arguments.of(entity4),
				Arguments.of(entity5),
				Arguments.of(entity6));
		//@formatter:on
	}

	@ParameterizedTest
	@MethodSource("provideInvalidInsertHeaderEntity")
	void insertShouldFail(ArticleHeaderEntity articleHeaderEntity) {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleHeaderRepository.insert(service, articleHeaderEntity))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	@Test
	void modifyArticleHeader() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");

		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setTitle(title);
		articleHeaderEntity.setBoardId(boardId);
		articleHeaderEntity.setWriter(writer);
		articleHeaderEntity.setActionDateTime(new ActionDateTime());
		articleHeaderEntity.getActionDateTime().setUpdatedAt(ZonedDateTime.now());

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		var updateCaptor = ArgumentCaptor.forClass(Update.class);
		var findAndModifyCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);
		given(this.mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
				eq(ArticleHeaderEntity.class)))
			.willReturn(articleHeaderEntity);

		// when
		var modifiedHeaderEntity = this.articleHeaderRepository.modifyArticleHeader(service, id, title);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		assertThat(readPreferenceCaptor.getValue()).isNotNull();
		assertThat(readPreferenceCaptor.getValue()).isEqualTo(ReadPreference.primary());

		verify(this.mongoTemplate).findAndModify(queryCaptor.capture(), updateCaptor.capture(),
				findAndModifyCaptor.capture(), eq(ArticleHeaderEntity.class));
		assertThat(queryCaptor.getValue()).isNotNull();
		assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo(id);
		assertThat(updateCaptor.getValue()).isNotNull();
		Document doc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
		assertThat(doc.get("title")).isEqualTo(title);
		assertThat(doc.get("actionDateTime.updatedAt")).isNotNull();
		assertThat(findAndModifyCaptor.getValue()).isNotNull();
		assertThat(findAndModifyCaptor.getValue().isReturnNew()).isTrue();

		assertThat(modifiedHeaderEntity).isNotNull();
		assertThat(modifiedHeaderEntity.getId()).isEqualTo(id);
		assertThat(modifiedHeaderEntity.getBoardId()).isEqualTo(boardId);
		assertThat(modifiedHeaderEntity.getTitle()).isEqualTo(title);
		assertThat(modifiedHeaderEntity.getWriter()).isNotNull();
		assertThat(modifiedHeaderEntity.getWriter().getWid()).isEqualTo(writer.getWid());
		assertThat(modifiedHeaderEntity.getActionDateTime()).isNotNull();
		assertThat(modifiedHeaderEntity.getActionDateTime().getCreatedAt()).isNotNull();
		assertThat(modifiedHeaderEntity.getActionDateTime().getUpdatedAt()).isNotNull();
	}

	static Stream<Arguments> provideInvalidModifyArgs() {
		//@formatter:off
		return Stream.of(
				Arguments.of("", "title"),
				Arguments.of("id", ""));
		//@formatter:on
	}

	@ParameterizedTest
	@MethodSource("provideInvalidModifyArgs")
	void modifyShouldFail(String id, String title) {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleHeaderRepository.modifyArticleHeader(service, id, title))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	@Test
	void incViewCount() {
		// given
		var service = "jenie-test";
		var id = "article-id";
		var inc = 1;

		var reaction = new Reaction();
		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setReaction(reaction);
		articleHeaderEntity.getReaction().setViewCount(articleHeaderEntity.getReaction().getViewCount() + inc);

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		var updateCaptor = ArgumentCaptor.forClass(Update.class);
		var findAndModifyCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);
		given(this.mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
				eq(ArticleHeaderEntity.class)))
			.willReturn(articleHeaderEntity);

		// when
		var result = this.articleHeaderRepository.incViewCount(service, id, inc);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		assertThat(readPreferenceCaptor.getValue()).isNotNull();
		assertThat(readPreferenceCaptor.getValue()).isEqualTo(ReadPreference.primary());

		verify(this.mongoTemplate).findAndModify(queryCaptor.capture(), updateCaptor.capture(),
				findAndModifyCaptor.capture(), eq(ArticleHeaderEntity.class));
		assertThat(queryCaptor.getValue()).isNotNull();
		assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo(id);
		assertThat(updateCaptor.getValue()).isNotNull();
		Document doc = (Document) updateCaptor.getValue().getUpdateObject().get("$inc");
		assertThat(doc.get("reaction.viewCount")).isEqualTo(inc);
		assertThat(findAndModifyCaptor.getValue()).isNotNull();
		assertThat(findAndModifyCaptor.getValue().isReturnNew()).isTrue();

		assertThat(result).isNotNull();
		assertThat(result.getReaction().getViewCount()).isEqualTo(articleHeaderEntity.getReaction().getViewCount());
	}

	@Test
	void incViewCountShouldFail() {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleHeaderRepository.incViewCount(service, "", 1))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	@Test
	void deleteArticle() {
		// given
		var service = "jenie-test";
		var id = "article-id";

		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setState(ArticleState.Deleted.getCode());
		articleHeaderEntity.setActionDateTime(new ActionDateTime());
		articleHeaderEntity.getActionDateTime().setDeletedAt(ZonedDateTime.now());

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		var updateCaptor = ArgumentCaptor.forClass(Update.class);
		var findAndModifyCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);
		given(this.mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
				eq(ArticleHeaderEntity.class)))
			.willReturn(articleHeaderEntity);

		// when
		var result = this.articleHeaderRepository.deleteArticle(service, id);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		assertThat(readPreferenceCaptor.getValue()).isNotNull();
		assertThat(readPreferenceCaptor.getValue()).isEqualTo(ReadPreference.primary());

		verify(this.mongoTemplate).findAndModify(queryCaptor.capture(), updateCaptor.capture(),
				findAndModifyCaptor.capture(), eq(ArticleHeaderEntity.class));
		assertThat(queryCaptor.getValue()).isNotNull();
		assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo(id);
		assertThat(updateCaptor.getValue()).isNotNull();
		Document doc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
		assertThat(doc.get("state")).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(doc.get("actionDateTime.deletedAt")).isNotNull();
		assertThat(findAndModifyCaptor.getValue()).isNotNull();
		assertThat(findAndModifyCaptor.getValue().isReturnNew()).isTrue();

		assertThat(result).isNotNull();
		assertThat(result.getState()).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(result.getActionDateTime().getDeletedAt()).isNotNull();
	}

	@Test
	void deleteShouldFail() {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleHeaderRepository.deleteArticle(service, ""))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

}
