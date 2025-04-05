package org.jenie.spring.data.mongodb.operation;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public interface ReactiveMongoTemplateRouter {

	Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern);

	Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey);

	Mono<ReactiveMongoTransactionManager> transactionManager(String dbKey);

}
