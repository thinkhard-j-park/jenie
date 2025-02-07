package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.util.StringUtils;

public class ReactiveCaffeineMongoTemplateRouter implements ReactiveMongoTemplateRouter {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveCaffeineMongoTemplateRouter.class);

	private final LoadingCache<String, Mono<ReactiveMongoTransactionManager>> transactionManagerCache;

	private final LoadingCache<MongoTemplateKey, Mono<ReactiveMongoTemplate>> mongoTemplateCache;

	public ReactiveCaffeineMongoTemplateRouter(ReactiveMongoDBConnectorRegistry connectorRegistry) {
		LoadingCache<String, Mono<DBConn>> dbConnCache = Caffeine.newBuilder()
			.build(new ReactiveCaffeineMongoTemplateRouter.DBConnLoader(connectorRegistry));

		LoadingCache<String, Mono<ReactiveMongoDatabaseFactory>> databaseFactoryCache = Caffeine.newBuilder()
			.build(new ReactiveCaffeineMongoTemplateRouter.MongoDatabaseFactoryLoader(connectorRegistry, dbConnCache));

		this.transactionManagerCache = Caffeine.newBuilder()
			.build(new ReactiveCaffeineMongoTemplateRouter.MongoTransactionManagerLoader(databaseFactoryCache));

		this.mongoTemplateCache = Caffeine.newBuilder()
			.build(new ReactiveCaffeineMongoTemplateRouter.MongoTemplateLoader(connectorRegistry, dbConnCache,
					databaseFactoryCache));
		logger.info("CaffeineReactiveMongoTemplateRouter is initialized");
	}

	@Override
	public Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey, ReadPreference readPreference,
			WriteConcern writeConcern) {
		if (!StringUtils.hasText(dbKey)) {
			return Mono.error(new IllegalArgumentException("dbKey must not be empty"));
		}
		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);
		return this.mongoTemplateCache.get(k);
	}

	@Override
	public Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey) {
		return this.mongoTemplate(dbKey, ReadPreference.secondaryPreferred(), null);
	}

	@Override
	public Mono<ReactiveMongoTransactionManager> transactionManager(String dbKey) {
		return this.transactionManagerCache.get(dbKey);
	}

	static class DBConnLoader implements CacheLoader<String, Mono<DBConn>> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		DBConnLoader(ReactiveMongoDBConnectorRegistry connectorRegistry) {
			this.connectorRegistry = connectorRegistry;
		}

		@Override
		public @Nullable Mono<DBConn> load(String key) {
			return this.connectorRegistry.getDBConn(key).cache();
		}

	}

	static class MongoDatabaseFactoryLoader implements CacheLoader<String, Mono<ReactiveMongoDatabaseFactory>> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		private final LoadingCache<String, Mono<DBConn>> dbConnCache;

		MongoDatabaseFactoryLoader(ReactiveMongoDBConnectorRegistry connectorRegistry,
				LoadingCache<String, Mono<DBConn>> dbConnCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
		}

		@Override
		public @Nullable Mono<ReactiveMongoDatabaseFactory> load(String key) {
			return this.dbConnCache.get(key).map((dbConn) -> {
				if (dbConn == null) {
					return Mono.error(new IllegalArgumentException("DBConn must not be null"));
				}

				var clusterKey = dbConn.getClusterKey();
				var dbName = dbConn.getDbName();
				var connector = this.connectorRegistry.getConnector(clusterKey);
				return new SimpleReactiveMongoDatabaseFactory(connector.getClient(), dbName);
			}).cast(ReactiveMongoDatabaseFactory.class).cache();

		}

	}

	static class MongoTransactionManagerLoader implements CacheLoader<String, Mono<ReactiveMongoTransactionManager>> {

		private final LoadingCache<String, Mono<ReactiveMongoDatabaseFactory>> databaseFactoryCache;

		MongoTransactionManagerLoader(LoadingCache<String, Mono<ReactiveMongoDatabaseFactory>> databaseFactoryCache) {
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @Nullable Mono<ReactiveMongoTransactionManager> load(String key) {
			return this.databaseFactoryCache.get(key).flatMap((factory) -> {
				if (factory == null) {
					return Mono.error(new IllegalArgumentException("ReactiveMongoDatabaseFactory must not be null"));
				}
				return Mono.just(new ReactiveMongoTransactionManager(factory));
			}).cache();
		}

	}

	static class MongoTemplateLoader implements CacheLoader<MongoTemplateKey, Mono<ReactiveMongoTemplate>> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		private final LoadingCache<String, Mono<DBConn>> dbConnCache;

		private final LoadingCache<String, Mono<ReactiveMongoDatabaseFactory>> databaseFactoryCache;

		MongoTemplateLoader(ReactiveMongoDBConnectorRegistry connectorRegistry,
				LoadingCache<String, Mono<DBConn>> dbConnCache,
				LoadingCache<String, Mono<ReactiveMongoDatabaseFactory>> databaseFactoryCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @Nullable Mono<ReactiveMongoTemplate> load(MongoTemplateKey key) {
			return Mono.zip(this.dbConnCache.get(key.dbKey()), this.databaseFactoryCache.get(key.dbKey())).map((t2) -> {
				var dbConn = t2.getT1();
				var factory = t2.getT2();

				var clusterKey = dbConn.getClusterKey();
				var connector = this.connectorRegistry.getConnector(clusterKey);

				var cluster = connector.getCluster();
				var writeConcern = (key.writeConcern() == null) ? cluster.writeConcern() : key.writeConcern();

				var template = new ReactiveMongoTemplate(factory, connector.getMappingMongoConverter());
				if (key.readPreference() != null) {
					configureReadPreference(key.readPreference(), cluster);
					template.setReadPreference(key.readPreference());
				}
				template.setWriteConcern(writeConcern);

				return template;
			}).cache();
		}

		private void configureReadPreference(ReadPreference readPreference, MongoDBCluster cluster) {
			if (readPreference == null) {
				return;
			}

			if (!"primary".equalsIgnoreCase(readPreference.getName())) {
				var replicaTagSets = new ArrayList<>(MongoDBCluster.replicaTagSets(cluster.getTagSet()));
				if (readPreference instanceof TaggableReadPreference taggableReadPreference) {
					replicaTagSets.addAll(taggableReadPreference.getTagSetList());
				}
				readPreference.withTagSetList(replicaTagSets);
			}
		}

	}

}
