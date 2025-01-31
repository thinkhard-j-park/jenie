package org.jenie.spring.helloworld.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.exception.CommonErrors;
import org.jenie.spring.helloworld.repository.sync.ArticleContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleContentRepositoryTests {

	@InjectMocks
	private ArticleContentRepository articleContentRepository;

	@Mock
	private MongoTemplateRouter mongoTemplateRouter;

	@Mock
	private MongoTemplate mongoTemplate;

	@Test
	void insert() {
		// given
		var service = "jenie-test";
		var id = new ObjectId().toString();
		var content = "hello world";

		var articleContentEntity = new ArticleContentEntity();
		articleContentEntity.setId(id);
		articleContentEntity.setContent(content);

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);
		given(this.mongoTemplate.insert(any(ArticleContentEntity.class))).willReturn(articleContentEntity);

		// when
		var createdContentEntity = this.articleContentRepository.insert(service, articleContentEntity);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		verify(this.mongoTemplate).insert(any(ArticleContentEntity.class));
		assertThat(createdContentEntity).isNotNull();
		assertThat(createdContentEntity.getId()).isEqualTo(id);
		assertThat(createdContentEntity.getContent()).isEqualTo(content);

		assertThat(createdContentEntity).isNotNull();
		assertThat(createdContentEntity.getId()).isEqualTo(id);
		assertThat(createdContentEntity.getContent()).isEqualTo(content);
	}

	@Test
	void insertShouldFail() {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleContentRepository.insert(service, null))
			.isInstanceOf(CommonErrors.IllegalDataException.class);

		// give
		var articleContentEntity = new ArticleContentEntity();

		// when, then
		assertThatThrownBy(() -> this.articleContentRepository.insert(service, articleContentEntity))
			.isInstanceOf(CommonErrors.IllegalDataException.class);

		// give
		articleContentEntity.setId("id");

		// when, then
		assertThatThrownBy(() -> this.articleContentRepository.insert(service, articleContentEntity))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	@Test
	void modifyArticleContent() {
		// given
		var service = "jenie-test";
		var id = new ObjectId().toString();
		var content = "hello world";

		var articleContentEntity = new ArticleContentEntity();
		articleContentEntity.setId(id);
		articleContentEntity.setContent(content);

		var readPreferenceCaptor = ArgumentCaptor.forClass(ReadPreference.class);
		given(this.mongoTemplateRouter.mongoTemplate(eq(service), any(ReadPreference.class), eq(WriteConcern.MAJORITY)))
			.willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		var updateCaptor = ArgumentCaptor.forClass(Update.class);
		var findAndModifyCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);
		given(this.mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
				eq(ArticleContentEntity.class)))
			.willReturn(articleContentEntity);

		// when
		var modifiedContentEntity = this.articleContentRepository.modifyArticleContent(service, id, content);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(eq(service), readPreferenceCaptor.capture(),
				eq(WriteConcern.MAJORITY));
		assertThat(readPreferenceCaptor.getValue()).isNotNull();
		assertThat(readPreferenceCaptor.getValue()).isEqualTo(ReadPreference.primary());

		verify(this.mongoTemplate).findAndModify(queryCaptor.capture(), updateCaptor.capture(),
				findAndModifyCaptor.capture(), eq(ArticleContentEntity.class));
		assertThat(queryCaptor.getValue()).isNotNull();
		assertThat(queryCaptor.getValue().getQueryObject().keySet()).hasSize(1);
		assertThat(queryCaptor.getValue().getQueryObject().get("_id").toString()).isEqualTo(id);
		assertThat(updateCaptor.getValue()).isNotNull();

		Document doc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
		assertThat(doc.get("content")).isEqualTo(content);
		assertThat(modifiedContentEntity).isNotNull();
		assertThat(modifiedContentEntity.getId()).isEqualTo(id);
		assertThat(modifiedContentEntity.getContent()).isEqualTo(content);

		assertThat(modifiedContentEntity).isNotNull();
		assertThat(modifiedContentEntity.getId()).isEqualTo(id);
		assertThat(modifiedContentEntity.getContent()).isEqualTo(content);
	}

	@Test
	void modifyShouldFail() {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleContentRepository.modifyArticleContent(service, null, null))
			.isInstanceOf(CommonErrors.IllegalDataException.class);

		assertThatThrownBy(() -> this.articleContentRepository.modifyArticleContent(service, "id", ""))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

	@Test
	void findArticleContentById() {
		// given
		var service = "jenie-test";
		var id = new ObjectId().toString();
		var content = "hello world";

		var articleContentEntity = new ArticleContentEntity();
		articleContentEntity.setId(id);
		articleContentEntity.setContent(content);

		given(this.mongoTemplateRouter.mongoTemplate(service)).willReturn(this.mongoTemplate);

		var queryCaptor = ArgumentCaptor.forClass(Query.class);
		given(this.mongoTemplate.findOne(any(Query.class), eq(ArticleContentEntity.class)))
			.willReturn(articleContentEntity);

		// when
		var result = this.articleContentRepository.findArticleContentById(service, id);

		// then
		verify(this.mongoTemplateRouter).mongoTemplate(service);

		verify(this.mongoTemplate).findOne(queryCaptor.capture(), eq(ArticleContentEntity.class));
		var capturedQuery = queryCaptor.getValue();
		assertThat(capturedQuery).isNotNull();
		assertThat(capturedQuery.getQueryObject().keySet()).hasSize(1);
		assertThat(capturedQuery.getQueryObject().get("_id").toString()).isEqualTo(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getContent()).isEqualTo(content);
	}

	@Test
	void findArticleContentByIdShouldFail() {
		// given
		var service = "jenie-test";

		// when, then
		assertThatThrownBy(() -> this.articleContentRepository.findArticleContentById(service, ""))
			.isInstanceOf(CommonErrors.IllegalDataException.class);
	}

}
