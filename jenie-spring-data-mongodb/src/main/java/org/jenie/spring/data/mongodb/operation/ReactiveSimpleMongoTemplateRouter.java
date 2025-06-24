package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.util.Assert;

public class ReactiveSimpleMongoTemplateRouter implements ReactiveMongoTemplateRouter {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveSimpleMongoTemplateRouter.class);

	private final ReactiveMongoDBConnectorRegistry connectorRegistry;

	private final ConcurrentHashMap<String, Mono<DBConn>> dbConnCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<String, SimpleReactiveMongoDatabaseFactory> databaseFactoryCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<MongoTemplateKey, Mono<ReactiveMongoTemplate>> mongoTemplateCache = new ConcurrentHashMap<>();

	public ReactiveSimpleMongoTemplateRouter(ReactiveMongoDBConnectorRegistry connectorRegistry) {
		this.connectorRegistry = connectorRegistry;
		logger.info("SimpleReactiveMongoTemplateRouter is initialized");
	}

	private Mono<DBConn> dbConn(String dbKey) {
		return this.dbConnCache.computeIfAbsent(dbKey, (k) -> this.connectorRegistry.getDBConn(k).cache());
	}

	private SimpleReactiveMongoDatabaseFactory databaseFactory(DBConn dbConn) {
		return this.databaseFactoryCache.computeIfAbsent(dbConn.getDbKey(), (key) -> {
			var clusterKey = dbConn.getClusterKey();
			var connector = this.connectorRegistry.getConnector(clusterKey);
			return new SimpleReactiveMongoDatabaseFactory(connector.getClient(), dbConn.getDbName());
		});
	}

	@Override
	public Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey, ReadPreference readPreference,
			WriteConcern writeConcern) {
		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);
		return this.mongoTemplateCache.computeIfAbsent(k, (key) -> dbConn(dbKey).map((dbConn) -> {
			var clusterKey = dbConn.getClusterKey();
			var connector = this.connectorRegistry.getConnector(clusterKey);
			var factory = databaseFactory(dbConn);
			Assert.notNull(factory, "ReactiveMongoDatabaseFactory must not be null");

			var cluster = connector.getCluster();
			var template = new ReactiveMongoTemplate(factory, connector.getMappingMongoConverter());
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
		}).cache());

	}

	@Override
	public Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey) {
		return this.mongoTemplate(dbKey, ReadPreference.secondaryPreferred(), null);
	}

	@Override
	public Mono<ReactiveMongoTransactionManager> transactionManager(String dbKey) {
		return dbConn(dbKey).map(this::databaseFactory).map(ReactiveMongoTransactionManager::new);
	}

}
