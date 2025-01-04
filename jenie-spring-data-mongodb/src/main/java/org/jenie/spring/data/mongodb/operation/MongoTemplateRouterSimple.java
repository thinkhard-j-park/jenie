package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.config.MongoDBCluster;
import org.jenie.spring.data.mongodb.config.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MongoTemplateRouterSimple implements MongoTemplateRouter {

	private static final Logger logger = LoggerFactory.getLogger(MongoTemplateRouterSimple.class);

	private final MongoDBConnectorRegistry connectorRegistry;

	private final ConcurrentHashMap<String, DBConn> dbConnCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<String, SimpleMongoClientDatabaseFactory> databaseFactoryCache = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<MongoTemplateKey, MongoTemplate> mongoTemplateCache = new ConcurrentHashMap<>();

	public MongoTemplateRouterSimple(MongoDBConnectorRegistry connectorRegistry) {
		this.connectorRegistry = connectorRegistry;
		logger.info("MongoTemplateRouterSimple is initialized");
	}

	private DBConn dbConn(String dbKey) {
		if (!this.dbConnCache.containsKey(dbKey)) {
			this.dbConnCache.putIfAbsent(dbKey, getDBConn(dbKey));
		}

		return this.dbConnCache.get(dbKey);
	}

	private DBConn getDBConn(String dbKey) {
		for (var dbConnTemplate : this.connectorRegistry.getTemplatesList()) {
			var query = Query.query(Criteria.where("dbKey").is(dbKey));
			var dbConn = dbConnTemplate.findOne(query, DBConn.class);
			if (dbConn != null && StringUtils.hasText(dbConn.getId())) {
				return dbConn;
			}

		}

		throw new DBConnNotFoundException("DBConn must not be null: " + dbKey);
	}

	private SimpleMongoClientDatabaseFactory databaseFactory(DBConn dbConn) {
		var clusterKey = dbConn.getClusterKey();
		var connector = this.connectorRegistry.getConnector(clusterKey);
		var databaseFactory = this.databaseFactoryCache.get(dbConn.getDbKey());
		if (!this.databaseFactoryCache.containsKey(dbConn.getDbKey())) {
			databaseFactory = new SimpleMongoClientDatabaseFactory(connector.getClient(), dbConn.getDbName());
			this.databaseFactoryCache.putIfAbsent(dbConn.getDbKey(), databaseFactory);
		}

		return this.databaseFactoryCache.get(dbConn.getDbKey());
	}

	@Override
	public MongoTemplate mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		var dbConn = dbConn(dbKey);

		var clusterKey = dbConn.getClusterKey();
		var connector = this.connectorRegistry.getConnector(clusterKey);
		var factory = databaseFactory(dbConn);
		Assert.notNull(factory, "MongoDatabaseFactory must not be null");

		var cluster = connector.getCluster();

		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);
		if (!this.mongoTemplateCache.containsKey(k)) {
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
			this.mongoTemplateCache.putIfAbsent(k, template);
		}

		return this.mongoTemplateCache.get(k);
	}

	@Override
	public MongoTemplate mongoTemplate(String dbKey) {
		var dbConn = dbConn(dbKey);

		var clusterKey = dbConn.getClusterKey();
		var connector = this.connectorRegistry.getConnector(clusterKey);
		var factory = databaseFactory(dbConn);
		Assert.notNull(factory, "MongoDatabaseFactory must not be null");

		var readPreference = ReadPreference.secondaryPreferred();
		var k = new MongoTemplateKey(dbKey, readPreference, null);
		if (!this.mongoTemplateCache.containsKey(k)) {
			var template = new MongoTemplate(factory, connector.getMappingMongoConverter());
			template.setReadPreference(readPreference);
			template.setWriteConcern(null);
			this.mongoTemplateCache.putIfAbsent(k, template);
		}

		return this.mongoTemplateCache.get(k);
	}

	@Override
	public MongoTransactionManager transactionManager(String dbKey) {
		var dbConn = dbConn(dbKey);
		var factory = databaseFactory(dbConn);
		return new MongoTransactionManager(factory);
	}

}
