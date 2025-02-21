package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.Assert;

public class SimpleMongoTemplateRouter implements MongoTemplateRouter {

	private static final Logger logger = LoggerFactory.getLogger(SimpleMongoTemplateRouter.class);

	private final MongoDBConnectorRegistry connectorRegistry;

	private final ConcurrentHashMap<String, DBConn> dbConnCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<String, SimpleMongoClientDatabaseFactory> databaseFactoryCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<MongoTemplateKey, MongoTemplate> mongoTemplateCache = new ConcurrentHashMap<>();

	public SimpleMongoTemplateRouter(MongoDBConnectorRegistry connectorRegistry) {
		this.connectorRegistry = connectorRegistry;
		logger.info("SimpleMongoTemplateRouter is initialized");
	}

	private DBConn dbConn(String dbKey) {
		return this.dbConnCache.computeIfAbsent(dbKey, this.connectorRegistry::getDBConn);
	}

	private SimpleMongoClientDatabaseFactory databaseFactory(DBConn dbConn) {
		var clusterKey = dbConn.getClusterKey();
		var connector = this.connectorRegistry.getConnector(clusterKey);
		return this.databaseFactoryCache.computeIfAbsent(dbConn.getDbKey(),
				(k) -> new SimpleMongoClientDatabaseFactory(connector.getClient(), dbConn.getDbName()));
	}

	@Override
	public MongoTemplate mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);

		return this.mongoTemplateCache.computeIfAbsent(k, (key) -> {
			var dbConn = dbConn(dbKey);
			var clusterKey = dbConn.getClusterKey();
			var connector = this.connectorRegistry.getConnector(clusterKey);
			var factory = databaseFactory(dbConn);
			Assert.notNull(factory, "MongoDatabaseFactory must not be null");

			var cluster = connector.getCluster();
			var template = new MongoTemplate(factory, connector.getMappingMongoConverter());
			if (!"primary".equalsIgnoreCase(readPreference.getName())) {
				var replicaTagSets = new ArrayList<>(MongoDBCluster.replicaTagSets(cluster.getTagSet()));
				if (readPreference instanceof TaggableReadPreference taggableReadPreference) {
					replicaTagSets.addAll(taggableReadPreference.getTagSetList());
				}
				readPreference.withTagSetList(replicaTagSets);
			}
			template.setReadPreference(readPreference);
			template.setWriteConcern(writeConcern);
			return template;
		});
	}

	@Override
	public MongoTemplate mongoTemplate(String dbKey) {
		var readPreference = ReadPreference.secondaryPreferred();
		var k = new MongoTemplateKey(dbKey, readPreference, null);

		return this.mongoTemplateCache.computeIfAbsent(k, (key) -> {
			var dbConn = dbConn(dbKey);
			var clusterKey = dbConn.getClusterKey();
			var connector = this.connectorRegistry.getConnector(clusterKey);
			var factory = databaseFactory(dbConn);
			Assert.notNull(factory, "MongoDatabaseFactory must not be null");

			var template = new MongoTemplate(factory, connector.getMappingMongoConverter());
			template.setReadPreference(readPreference);
			template.setWriteConcern(null);
			return template;
		});
	}

	@Override
	public MongoTransactionManager transactionManager(String dbKey) {
		var dbConn = dbConn(dbKey);
		var factory = databaseFactory(dbConn);
		return new MongoTransactionManager(factory);
	}

}
