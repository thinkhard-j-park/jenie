package org.jenie.spring.helloworld.repository.reactive;

import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.ReactiveAssertHelper;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@ConditionalOnReactive
@Repository
public class ReactiveBoardRepository extends ReactiveMongoDBRepository {

	public ReactiveBoardRepository(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public Mono<BoardEntity> findBoardById(String dbKey, String id) {
		return ReactiveAssertHelper.validObjectId(id, "id should be valid")
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey))
			.flatMap((t) -> t.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))), BoardEntity.class));

	}

}
