package org.jenie.spring.data.mongodb.operation;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
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
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

public class ReactiveCaffeineMongoTemplateRouter implements ReactiveMongoTemplateRouter {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveCaffeineMongoTemplateRouter.class);

	private final AsyncLoadingCache<String, ReactiveMongoTransactionManager> transactionManagerCache;

	private final AsyncLoadingCache<MongoTemplateKey, ReactiveMongoTemplate> mongoTemplateCache;

	public ReactiveCaffeineMongoTemplateRouter(ReactiveMongoDBConnectorRegistry connectorRegistry) {
		AsyncLoadingCache<String, DBConn> dbConnCache = Caffeine.newBuilder()
			.buildAsync(new ReactiveCaffeineMongoTemplateRouter.DBConnLoader(connectorRegistry));

		AsyncLoadingCache<String, ReactiveMongoDatabaseFactory> databaseFactoryCache = Caffeine.newBuilder()
			.buildAsync(
					new ReactiveCaffeineMongoTemplateRouter.MongoDatabaseFactoryLoader(connectorRegistry, dbConnCache));

		this.transactionManagerCache = Caffeine.newBuilder()
			.buildAsync(new ReactiveCaffeineMongoTemplateRouter.MongoTransactionManagerLoader(databaseFactoryCache));

		this.mongoTemplateCache = Caffeine.newBuilder()
			.buildAsync(new ReactiveCaffeineMongoTemplateRouter.MongoTemplateLoader(connectorRegistry, dbConnCache,
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
		return Mono.fromFuture(this.mongoTemplateCache.get(k));
	}

	@Override
	public Mono<ReactiveMongoTemplate> mongoTemplate(String dbKey) {
		return this.mongoTemplate(dbKey, ReadPreference.secondaryPreferred(), null);
	}

	@Override
	public Mono<ReactiveMongoTransactionManager> transactionManager(String dbKey) {
		return Mono.fromFuture(this.transactionManagerCache.get(dbKey));
	}

	static class DBConnLoader implements AsyncCacheLoader<String, DBConn> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		DBConnLoader(ReactiveMongoDBConnectorRegistry connectorRegistry) {
			this.connectorRegistry = connectorRegistry;
		}

		@Override
		public @NonNull CompletableFuture<? extends DBConn> asyncLoad(@NonNull String key, @NonNull Executor executor) {
			return this.connectorRegistry.getDBConn(key).toFuture();
		}

	}

	static class MongoDatabaseFactoryLoader implements AsyncCacheLoader<String, ReactiveMongoDatabaseFactory> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		private final AsyncLoadingCache<String, DBConn> dbConnCache;

		MongoDatabaseFactoryLoader(ReactiveMongoDBConnectorRegistry connectorRegistry,
				AsyncLoadingCache<String, DBConn> dbConnCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
		}

		@Override
		public @NonNull CompletableFuture<? extends ReactiveMongoDatabaseFactory> asyncLoad(@NonNull String key,
				@NonNull Executor executor) throws Exception {
			return this.dbConnCache.get(key).thenApply((dbConn) -> {
				var connector = this.connectorRegistry.getConnector(dbConn.getClusterKey());
				return new SimpleReactiveMongoDatabaseFactory(connector.getClient(), dbConn.getDbName());
			});
		}

	}

	static class MongoTransactionManagerLoader implements AsyncCacheLoader<String, ReactiveMongoTransactionManager> {

		private final AsyncLoadingCache<String, ReactiveMongoDatabaseFactory> databaseFactoryCache;

		MongoTransactionManagerLoader(AsyncLoadingCache<String, ReactiveMongoDatabaseFactory> databaseFactoryCache) {
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @NonNull CompletableFuture<ReactiveMongoTransactionManager> asyncLoad(@NonNull String key,
				@NonNull Executor executor) {
			return this.databaseFactoryCache.get(key).thenApply(ReactiveMongoTransactionManager::new);
		}

	}

	static class MongoTemplateLoader implements AsyncCacheLoader<MongoTemplateKey, ReactiveMongoTemplate> {

		private final ReactiveMongoDBConnectorRegistry connectorRegistry;

		private final AsyncLoadingCache<String, DBConn> dbConnCache;

		private final AsyncLoadingCache<String, ReactiveMongoDatabaseFactory> databaseFactoryCache;

		MongoTemplateLoader(ReactiveMongoDBConnectorRegistry connectorRegistry,
				AsyncLoadingCache<String, DBConn> dbConnCache,
				AsyncLoadingCache<String, ReactiveMongoDatabaseFactory> databaseFactoryCache) {
			this.connectorRegistry = connectorRegistry;
			this.dbConnCache = dbConnCache;
			this.databaseFactoryCache = databaseFactoryCache;
		}

		@Override
		public @NonNull CompletableFuture<ReactiveMongoTemplate> asyncLoad(@NonNull MongoTemplateKey key,
				@NonNull Executor executor) {
			var dbKey = key.dbKey();

			CompletableFuture<DBConn> dbConnFuture = this.dbConnCache.get(dbKey);
			CompletableFuture<ReactiveMongoDatabaseFactory> factoryFuture = this.databaseFactoryCache.get(dbKey);

			return dbConnFuture.thenCombine(factoryFuture, (dbConn, factory) -> {
				var clusterKey = dbConn.getClusterKey();
				var connector = this.connectorRegistry.getConnector(clusterKey);

				var cluster = connector.getCluster();
				var writeConcern = (key.writeConcern() == null) ? cluster.writeConcern() : key.writeConcern();

				var converter = connector.getMappingMongoConverter();
				var template = new ReactiveMongoTemplate(factory, converter);

				if (key.readPreference() != null) {
					configureReadPreference(key.readPreference(), cluster);
					template.setReadPreference(key.readPreference());
				}

				template.setWriteConcern(writeConcern);
				return template;
			});
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
