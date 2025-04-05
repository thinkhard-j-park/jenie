package org.jenie.spring.helloworld.reactive.repository;

import java.time.ZonedDateTime;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.ReactiveAssertHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@ConditionalOnReactive
@Repository
public class ReactiveArticleHeaderRepository extends ReactiveMongoDBRepository {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveArticleHeaderRepository.class);

	public ReactiveArticleHeaderRepository(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	/**
	 * Retrieves ArticleHeaderEntity.
	 * @param dbKey key used to find the cluster, database.
	 * @param id article ID.
	 * @param latest if set to true, reads data from the Primary. Otherwise, uses
	 * SecondaryPreferred. Note that when using SecondaryPreferred, the data might not be
	 * the latest during the ReplicaSet sync period.
	 * @return data read from the database.
	 */
	public Mono<ArticleHeaderEntity> findArticleHeaderById(String dbKey, String id, boolean latest) {
		return ReactiveAssertHelper.validObjectId(id, "id should be valid").then(Mono.defer(() -> {
			var readPreference = latest ? ReadPreference.primary() : ReadPreference.secondaryPreferred();
			return this.mongoTemplateRouter.mongoTemplate(dbKey, readPreference, null);
		}))
			.flatMap((t) -> t.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),
					ArticleHeaderEntity.class));
	}

	public Mono<Writer> findArticleWriterById(String dbKey, String id) {
		return ReactiveAssertHelper.validObjectId(id, "id should be valid")
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey))
			.flatMap((t) -> {
				var query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
				query.fields().include("writer");
				return t.findOne(query, ArticleHeaderEntity.class);
			})
			.map(ArticleHeaderEntity::getWriter);
	}

	public Flux<ArticleHeaderEntity> listArticleHeader(String dbKey, ListArticleHeaderRequestParam param) {
		var criteria = Criteria.where("state").is(ArticleState.Normal.getCode());
		if (StringUtils.hasText(param.getBoardId())) {
			criteria.and("boardId").is(param.getBoardId());
		}

		var sortOrder = SortOrder.fromCode(param.getSort());
		if (StringUtils.hasText(param.getPrevArticleId())) {
			switch (sortOrder) {
				case SortOrder.TIME_DESC -> criteria.and("_id").lt(new ObjectId(param.getPrevArticleId()));
				case SortOrder.TIME_ASC -> criteria.and("_id").gt(new ObjectId(param.getPrevArticleId()));
			}
		}
		var sort = Sort.by(sortOrder.getDirection(), sortOrder.getField());
		var query = Query.query(criteria).with(sort).limit(param.getSize() + 1);
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.flatMapMany((t) -> t.find(query, ArticleHeaderEntity.class));
	}

	public Mono<ArticleHeaderEntity> insert(String dbKey, ArticleHeaderEntity header) {
		return this.validateHeader(header)
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> {
				header.setActionDateTime(new ActionDateTime());
				return t.insert(header);
			});
	}

	private Mono<ArticleHeaderEntity> validateHeader(ArticleHeaderEntity header) {
		return Mono
			.zip(ReactiveAssertHelper.hasText(header.getBoardId(), "boardId is required"),
					ReactiveAssertHelper.hasText(header.getTitle(), "title is required"),
					ReactiveAssertHelper.notNull(header.getWriter(), "writer is required"),
					ReactiveAssertHelper.hasText(header.getWriter().getWid(), "writerId is required"),
					ReactiveAssertHelper.hasText(header.getWriter().getName(), "writerName is required"))
			.then(Mono.just(header));
	}

	public Mono<ArticleHeaderEntity> modifyArticleHeader(String dbKey, String id, String title) {
		return Mono
			.zip(ReactiveAssertHelper.validObjectId(id, "id should be valid"),
					ReactiveAssertHelper.hasText(title, "title is required"))
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> {
				var query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
				var update = new Update();
				update.set("title", title);
				update.set("actionDateTime.updatedAt", ZonedDateTime.now());
				var option = FindAndModifyOptions.options().returnNew(true);
				return t.findAndModify(query, update, option, ArticleHeaderEntity.class);
			});
	}

	public Mono<ArticleHeaderEntity> incViewCount(String dbKey, String id, Number number) {
		return ReactiveAssertHelper.validObjectId(id, "id should be valid")
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> {
				var query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
				var update = new Update();
				update.inc("reaction.viewCount", number);
				var option = FindAndModifyOptions.options().returnNew(true);
				return t.findAndModify(query, update, option, ArticleHeaderEntity.class);
			});
	}

	public Mono<ArticleHeaderEntity> deleteArticle(String dbKey, String id) {
		return ReactiveAssertHelper.validObjectId(id, "id should be valid")
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY))
			.flatMap((t) -> {
				var query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
				var update = new Update();
				update.set("state", ArticleState.Deleted.getCode());
				update.set("actionDateTime.deletedAt", ZonedDateTime.now());
				var option = FindAndModifyOptions.options().returnNew(true);
				return t.findAndModify(query, update, option, ArticleHeaderEntity.class);
			});
	}

}
