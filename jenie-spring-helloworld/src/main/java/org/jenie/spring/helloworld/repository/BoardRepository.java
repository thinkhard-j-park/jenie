package org.jenie.spring.helloworld.repository;

import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepository extends MongoDBRepository {

	public BoardRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public BoardEntity findBoardById(String dbKey, String id) {
		AssertHelper.validObjectId(id, "id should be valid");

		return this.mongoTemplateRouter.mongoTemplate(dbKey)
			.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))), BoardEntity.class);
	}

}
