package org.jenie.spring.helloworld.reactive.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.exception.ReactiveAssertHelper;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@ConditionalOnReactive
@Repository
public class ReactiveArticleContentRepository extends ReactiveMongoDBRepository {

	public ReactiveArticleContentRepository(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public Mono<ArticleContentEntity> insert(String dbKey, ArticleContentEntity content) {
		return Mono
			.zip(ReactiveAssertHelper.notNull(content, "ArticleContentEntity is required"),
					ReactiveAssertHelper.validObjectId(content.getId(), "ArticleContentEntity id should be valid"),
					ReactiveAssertHelper.hasText(content.getContent(), "content is required"))
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> t.insert(content))
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<ArticleContentEntity> modifyArticleContent(String dbKey, String articleId, String content) {
		return Mono
			.zip(ReactiveAssertHelper.validObjectId(articleId, "id should be provided"),
					ReactiveAssertHelper.hasText(content, "content is required"))
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> {
				var query = Query.query(Criteria.where("_id").is(new ObjectId(articleId)));
				var update = Update.update("content", content);
				var option = FindAndModifyOptions.options().returnNew(true);
				return t.findAndModify(query, update, option, ArticleContentEntity.class)
					.subscribeOn(Schedulers.boundedElastic());
			});
	}

	public Mono<ArticleContentEntity> findArticleContentById(String dbKey, String id) {
		return ReactiveAssertHelper.validObjectId(id, "id should be provided")
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey))
			.flatMap((t) -> t.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),
					ArticleContentEntity.class))
			.subscribeOn(Schedulers.boundedElastic());
	}

}
