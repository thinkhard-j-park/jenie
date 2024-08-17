package org.jenie.spring.helloworld.repository;


import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;

import org.springframework.stereotype.Repository;

@Repository
public class ArticleContentRepository {

	private final MongoTemplateRouter mongoTemplateRouter;

	public ArticleContentRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}
}
