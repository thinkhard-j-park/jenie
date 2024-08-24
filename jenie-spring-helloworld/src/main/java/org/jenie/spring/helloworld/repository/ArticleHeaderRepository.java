package org.jenie.spring.helloworld.repository;

import java.util.List;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.pojo.ActionDateTime;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Repository
public class ArticleHeaderRepository extends MongoDBRepository {

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public ArticleHeaderEntity findById(String dbKey, String id) {
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleHeaderEntity.class);
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
		Assert.hasText(header.getBoardId(), "boardId is required");
		Assert.hasText(header.getTitle(), "title is required");
		Assert.notNull(header.getWriter(), "writer is required");
		Assert.hasText(header.getWriter().getWid(), "writerId is required");
		Assert.hasText(header.getWriter().getName(), "writerName is required");
	}

}
