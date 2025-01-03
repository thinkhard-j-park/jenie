package org.jenie.spring.helloworld.repository;

import java.time.ZonedDateTime;
import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ArticleHeaderRepository extends MongoDBRepository {

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
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
	public ArticleHeaderEntity findArticleHeaderById(String dbKey, String id, boolean latest) {
		AssertHelper.validObjectId(id, "id should be valid");

		var readPreference = latest ? ReadPreference.primary() : ReadPreference.secondaryPreferred();
		return this.mongoTemplateRouter.mongoTemplate(dbKey, readPreference, null)
			.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))), ArticleHeaderEntity.class);
	}

	public Writer findArticleWriterById(String dbKey, String id) {
		AssertHelper.validObjectId(id, "id should be valid");

		var query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
		query.fields().include("writer");
		var articleHeader = this.mongoTemplateRouter.mongoTemplate(dbKey).findOne(query, ArticleHeaderEntity.class);
		AssertHelper.notNull(articleHeader, "id is not valid");

		return articleHeader.getWriter();
	}

	public List<ArticleHeaderEntity> listArticleHeader(String dbKey, ListArticleHeaderRequestParam param) {
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
		return this.mongoTemplateRouter.mongoTemplate(dbKey).find(query, ArticleHeaderEntity.class);
	}

	public ArticleHeaderEntity insert(String dbKey, ArticleHeaderEntity header) {
		this.validateHeader(header);
		header.setActionDateTime(new ActionDateTime());
		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.insert(header);
	}

	private void validateHeader(ArticleHeaderEntity header) {
		AssertHelper.hasText(header.getBoardId(), "boardId is required");
		AssertHelper.hasText(header.getTitle(), "title is required");
		AssertHelper.notNull(header.getWriter(), "writer is required");
		AssertHelper.hasText(header.getWriter().getWid(), "writerId is required");
		AssertHelper.hasText(header.getWriter().getName(), "writerName is required");
	}

	public ArticleHeaderEntity modifyArticleHeader(String dbKey, String id, String title) {
		AssertHelper.validObjectId(id, "id should be valid");
		AssertHelper.hasText(title, "title is required");

		var update = new Update();
		update.set("title", title);
		update.set("actionDateTime.updatedAt", ZonedDateTime.now());
		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(new ObjectId(id))), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
	}

	@Async
	public void incViewCountAsync(String dbKey, String id, Number number) {
		AssertHelper.validObjectId(id, "id should be valid");

		this.incViewCount(dbKey, id, number);
	}

	public ArticleHeaderEntity incViewCount(String dbKey, String id, Number number) {
		AssertHelper.validObjectId(id, "id should be valid");

		var update = new Update();
		update.inc("reaction.viewCount", number);
		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(new ObjectId(id))), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
	}

	public ArticleHeaderEntity deleteArticle(String dbKey, String id) {
		AssertHelper.validObjectId(id, "id should be valid");

		var update = new Update();
		update.set("state", ArticleState.Deleted.getCode());
		update.set("actionDateTime.deletedAt", ZonedDateTime.now());
		return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(new ObjectId(id))), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
	}

}
