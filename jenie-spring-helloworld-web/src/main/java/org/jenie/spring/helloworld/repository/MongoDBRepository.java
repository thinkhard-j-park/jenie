package org.jenie.spring.helloworld.repository;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;

abstract class MongoDBRepository {

	protected final MongoTemplateRouter mongoTemplateRouter;

	MongoDBRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

}
