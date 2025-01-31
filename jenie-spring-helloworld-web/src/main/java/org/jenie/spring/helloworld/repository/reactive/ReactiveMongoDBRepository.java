package org.jenie.spring.helloworld.repository.reactive;

import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;

abstract class ReactiveMongoDBRepository {

	protected final ReactiveMongoTemplateRouter mongoTemplateRouter;

	ReactiveMongoDBRepository(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

}
