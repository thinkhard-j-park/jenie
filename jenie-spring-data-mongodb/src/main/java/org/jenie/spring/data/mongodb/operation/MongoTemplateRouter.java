package org.jenie.spring.data.mongodb.operation;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoTemplateRouter {

	MongoTemplate mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern);

	MongoTemplate mongoTemplate(String dbKey);

	MongoTransactionManager transactionManager(String dbKey);

}
