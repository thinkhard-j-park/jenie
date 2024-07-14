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

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

public class MongoTemplateRouter {

	private final LoadingCache<MongoTemplateKey, MongoTemplate> cache;

	public MongoTemplateRouter(MongoDBConnectorRegistry connectorRegistry) {
		this.cache = Caffeine.newBuilder().build(new MongoTemplateLoader(connectorRegistry));
	}

	public MongoTemplate mongoTemplate(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		Assert.hasText(dbKey, "databaseKey must not be empty");
		var k = new MongoTemplateKey(dbKey, readPreference, writeConcern);
		return this.cache.get(k);
	}

	public MongoTemplate mongoTemplate(String dbKey) {
		return this.mongoTemplate(dbKey, null, null);
	}

	static class MongoTemplateLoader implements CacheLoader<MongoTemplateKey, MongoTemplate> {

		private final MongoDBConnectorRegistry connectorRegistry;

		MongoTemplateLoader(MongoDBConnectorRegistry connectorRegistry) {
			this.connectorRegistry = connectorRegistry;
		}

		@Override
		public @Nullable MongoTemplate load(MongoTemplateKey key) {
			for (var dbConnTemplate : this.connectorRegistry.getTemplatesList()) {
				var query = Query.query(Criteria.where("dbKey").is(key.dbKey()));
				var dbConn = dbConnTemplate.findOne(query, DBConn.class);

				if (dbConn != null) {
					var clusterKey = dbConn.getClusterKey();
					var dbName = dbConn.getDbName();
					var connector = this.connectorRegistry.getConnector(clusterKey);
					var factory = new SimpleMongoClientDatabaseFactory(connector.getClient(), dbName);
					var cluster = connector.getCluster();
					var writeConcern = (key.writeConcern() == null) ? cluster.writeConcern() : key.writeConcern();
					factory.setWriteConcern(writeConcern);

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
			return null;
		}

	}

}
