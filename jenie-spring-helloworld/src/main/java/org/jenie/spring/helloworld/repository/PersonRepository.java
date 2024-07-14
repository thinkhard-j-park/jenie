package org.jenie.spring.helloworld.repository;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.Person;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository {

	private final MongoTemplateRouter mongoTemplateRouter;

	public PersonRepository(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

	public Person findById(String dbKey, String id) {
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), Person.class);
	}

}
