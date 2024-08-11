package org.jenie.spring.helloworld.repository;

import java.util.List;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ArticleHeaderRepository {

	private final MongoTemplateRouter mongoTemplateRouter;

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
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

}
