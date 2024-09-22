package org.jenie.spring.helloworld.repository;

import org.jenie.spring.test.data.mongodb.operation.MongoTemplateRouter;

import org.springframework.stereotype.Repository;

@Repository
public class MongoDBRepository {

	protected final MongoTemplateRouter mongoTemplateRouter;

	public MongoDBRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}
}
