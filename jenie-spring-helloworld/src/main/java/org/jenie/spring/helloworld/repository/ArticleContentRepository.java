package org.jenie.spring.helloworld.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleContentRepository extends MongoDBRepository {

	public ArticleContentRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public ArticleContentEntity insert(String dbKey, ArticleContentEntity content) {
		AssertHelper.notNull(content, "ArticleContentEntity is required");
		AssertHelper.hasText(content.getId(), "ArticleContentEntity id should be provided");
		AssertHelper.hasText(content.getContent(), "content is required");

		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.insert(content);
	}

	public ArticleContentEntity modifyArticleContent(String dbKey, String articleId, String content) {
		AssertHelper.hasText(content, "content is required");

		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(articleId)), Update.update("content", content),
					FindAndModifyOptions.options().returnNew(true), ArticleContentEntity.class);
	}

	public ArticleContentEntity findArticleContentById(String dbKey, String id) {
		AssertHelper.hasText(id, "id is required");

		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleContentEntity.class);
	}

}
