package org.jenie.spring.helloworld.reactive.repository;

import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@ConditionalOnReactive
@Repository
public class ReactiveBoardRepository extends ReactiveMongoDBRepository {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveBoardRepository.class);

	public ReactiveBoardRepository(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public Mono<BoardEntity> findBoardById(String dbKey, String id) {
		return Mono.fromRunnable(() -> AssertHelper.validObjectId(id, "id should be valid"))
			.then(this.mongoTemplateRouter.mongoTemplate(dbKey))
			.flatMap((t) -> t.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))), BoardEntity.class));
	}

}
