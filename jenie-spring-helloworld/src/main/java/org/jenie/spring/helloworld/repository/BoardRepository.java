package org.jenie.spring.helloworld.repository;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.board.BoardEntity;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepository extends MongoDBRepository {

	public BoardRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public BoardEntity findBoardById(String dbKey, String id) {
		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(id)), BoardEntity.class);
	}

}
