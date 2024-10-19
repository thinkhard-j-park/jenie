package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jenie.spring.data.mongodb.config.MongoDBCluster;
import org.jenie.spring.data.mongodb.config.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;

import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MongoTemplateRouter {

	private final LoadingCache<String, MongoTransactionManager> transactionManagerCache;

	private final LoadingCache<MongoTemplateKey, MongoTemplate> mongoTemplateCache;

	public MongoTemplateRouter(MongoDBConnectorRegistry connectorRegistry) {
		LoadingCache<String, DBConn> dbConnCache = Caffeine.newBuilder().build(new DBConnLoader(connectorRegistry));
		LoadingCache<String, MongoDatabaseFactory> databaseFactoryCache = Caffeine.newBuilder()
			.build(new MongoDatabaseFactoryLoader(connectorRegistry, dbConnCache));

		this.transactionManagerCache = Caffeine.newBuilder()
			.build(new MongoTransactionManagerLoader(databaseFactoryCache));

		this.mongoTemplateCache = Caffeine.newBuilder()
			.build(new MongoTemplateLoader(connectorRegistry, dbConnCache, databaseFactoryCache));
	}

	public MongoTemplate mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		Assert.hasText(dbKey, "dbKey must not be empty");
		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);
		return this.mongoTemplateCache.get(k);
	}

	public MongoTemplate mongoTemplate(String dbKey) {
		return this.mongoTemplate(dbKey, ReadPreference.secondaryPreferred(), null);
	}

	public MongoTransactionManager transactionManager(String dbKey) {
		return this.transactionManagerCache.get(dbKey);
	}

	static class DBConnLoader implements CacheLoader<String, DBConn> {

		private final MongoDBConnectorRegistry connectorRegistry;

		DBConnLoader(MongoDBConnectorRegistry connectorRegistry) {
			this.connectorRegistry = connectorRegistry;
		}

		@Override
		public @Nullable DBConn load(String key) {
			for (var dbConnTemplate : this.connectorRegistry.getTemplatesList()) {
				var query = Query.query(Criteria.where("dbKey").is(key));
				var dbConn = dbConnTemplate.findOne(query, DBConn.class);
				if (dbConn != null && StringUtils.hasText(dbConn.getId())) {
					return dbConn;
				}
			}
			throw new DBConnNotFoundException("DBConn must not be null: " + key);
		}

	}

	static class MongoDatabaseFactoryLoader implements CacheLoader<String, MongoDatabaseFactory> {

		private final MongoDBConnectorRegistry connectorRegistry;

		private final LoadingCache<String, DBConn> dbConnCache;

		MongoDatabaseFactoryLoader(MongoDBConnectorRegistry connectorRegistry,
				LoadingCache<String, DBConn> dbConnCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
		}

		@Override
		public @Nullable MongoDatabaseFactory load(String key) {
			var dbConn = this.dbConnCache.get(key);
			Assert.notNull(dbConn, "DBConn must not be null");

			var clusterKey = dbConn.getClusterKey();
			var dbName = dbConn.getDbName();
			var connector = this.connectorRegistry.getConnector(clusterKey);
			return new SimpleMongoClientDatabaseFactory(connector.getClient(), dbName);
		}

	}

	static class MongoTransactionManagerLoader implements CacheLoader<String, MongoTransactionManager> {

		private final LoadingCache<String, MongoDatabaseFactory> databaseFactoryCache;

		MongoTransactionManagerLoader(LoadingCache<String, MongoDatabaseFactory> databaseFactoryCache) {
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @Nullable MongoTransactionManager load(String key) {
			var factory = this.databaseFactoryCache.get(key);
			Assert.notNull(factory, "MongoDatabaseFactory must not be null");
			return new MongoTransactionManager(factory);
		}

	}

	static class MongoTemplateLoader implements CacheLoader<MongoTemplateKey, MongoTemplate> {

		private final MongoDBConnectorRegistry connectorRegistry;

		private final LoadingCache<String, DBConn> dbConnCache;

		private final LoadingCache<String, MongoDatabaseFactory> databaseFactoryCache;

		MongoTemplateLoader(MongoDBConnectorRegistry connectorRegistry, LoadingCache<String, DBConn> dbConnCache,
				LoadingCache<String, MongoDatabaseFactory> databaseFactoryCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @Nullable MongoTemplate load(MongoTemplateKey key) {
			var dbConn = this.dbConnCache.get(key.dbKey());
			Assert.notNull(dbConn, "DBConn must not be null");

			var factory = this.databaseFactoryCache.get(key.dbKey());
			Assert.notNull(factory, "MongoDatabaseFactory must not be null");

			var clusterKey = dbConn.getClusterKey();
			var connector = this.connectorRegistry.getConnector(clusterKey);

			var cluster = connector.getCluster();
			var writeConcern = (key.writeConcern() == null) ? cluster.writeConcern() : key.writeConcern();

			var template = new MongoTemplate(factory, connector.getMappingMongoConverter());
			if (key.readPreference() != null && !"primary".equalsIgnoreCase(key.readPreference().getName())) {
				var replicaTagSets = new ArrayList<>(MongoDBCluster.replicaTagSets(cluster.getTagSet()));
				if (key.readPreference() instanceof TaggableReadPreference taggableReadPreference) {
					replicaTagSets.addAll(taggableReadPreference.getTagSetList());
				}
				key.readPreference().withTagSetList(replicaTagSets);
			}
			template.setReadPreference(key.readPreference());
			template.setWriteConcern(writeConcern);

			return template;
		}

	}

}
