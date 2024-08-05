package org.jenie.spring.helloworld.repository;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.ArticleHeader;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleHeaderRepository {

	private final MongoTemplateRouter mongoTemplateRouter;

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

	public ArticleHeader findById(String dbKey, String id) {
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleHeader.class);
	}

}
