package org.jenie.spring.helloworld.repository;

import java.time.ZonedDateTime;
import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;
import org.jenie.spring.helloworld.pojo.ActionDateTime;
import org.jenie.spring.helloworld.pojo.Writer;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ArticleHeaderRepository extends MongoDBRepository {

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public ArticleHeaderEntity findArticleHeaderById(String dbKey, String id) {
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleHeaderEntity.class);
	}

	public Writer findArticleWriterById(String dbKey, String id) {
		var query = Query.query(Criteria.where("_id").is(id));
		query.fields().include("writer");
		var articleHeader = this.mongoTemplateRouter.mongoTemplate(dbKey).findOne(query, ArticleHeaderEntity.class);
		if (articleHeader == null) {
			return null;
		}
		return articleHeader.getWriter();
	}

	public List<ArticleHeaderEntity> listArticleHeader(String dbKey, ListArticleHeaderRequestParam param) {
		var criteria = new Criteria();
		if (StringUtils.hasText(param.boardId())) {
			criteria.and("boardId").is(param.boardId());
		}

		var sortOrder = SortOrder.fromValue(param.sort());
		if (StringUtils.hasText(param.prevArticleId())) {
			switch (sortOrder) {
				case TIME_DESC -> criteria.and("id").lt(param.prevArticleId());
				case TIME_ASC -> criteria.and("id").gt(param.prevArticleId());
			}
		}
		var sort = Sort.by(sortOrder.getDirection(), sortOrder.getField());
		var query = Query.query(criteria).with(sort).limit(param.size() + 1);
		return this.mongoTemplateRouter.mongoTemplate(dbKey).find(query, ArticleHeaderEntity.class);
	}

	public ArticleHeaderEntity insert(String dbKey, ArticleHeaderEntity header) {
		return this.insert(this.mongoTemplateRouter.mongoTemplate(dbKey), header);
	}

	public ArticleHeaderEntity insert(MongoTemplate template, ArticleHeaderEntity header) {
		this.validateHeader(header);
		header.setActionDateTime(new ActionDateTime());
		return template.insert(header);
	}

	private void validateHeader(ArticleHeaderEntity header) {
		AssertHelper.hasText(header.getBoardId(), "boardId is required");
		AssertHelper.hasText(header.getTitle(), "title is required");
		AssertHelper.notNull(header.getWriter(), "writer is required");
		AssertHelper.hasText(header.getWriter().getWid(), "writerId is required");
		AssertHelper.hasText(header.getWriter().getName(), "writerName is required");
	}

	public ArticleHeaderEntity modifyArticleHeader(String service, String articleId, String title) {
		AssertHelper.hasText(title, "title is required");

		var update = new Update();
		update.set("title", title);
		update.set("actionDateTime.updatedAt", ZonedDateTime.now());
		return this.mongoTemplateRouter.mongoTemplate(service, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(articleId)), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
	}

}
