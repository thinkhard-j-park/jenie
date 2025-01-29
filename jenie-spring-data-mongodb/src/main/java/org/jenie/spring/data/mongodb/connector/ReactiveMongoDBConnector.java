package org.jenie.spring.data.mongodb.connector;

import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.TagSet;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.CollectionUtils;

public class ReactiveMongoDBConnector
		extends AbstractMongoDBConnector<MongoClient, ReactiveMongoDatabaseFactory, ReactiveMongoTemplate> {

	public ReactiveMongoDBConnector(MongoDBCluster cluster, MongoMappingContext mappingContext,
			MongoCustomConversions customConversions) {
		super(cluster, mappingContext, customConversions);
	}

	@Override
	protected MongoClient createMongoClients(MongoDBCluster cluster) {
		var clientSettings = mongoClientSettings(cluster);
		return MongoClients.create(clientSettings);
	}

	@Override
	protected SimpleReactiveMongoDatabaseFactory mongoDatabaseFactory(MongoClient client, String databaseName) {
		return new SimpleReactiveMongoDatabaseFactory(client, databaseName);
	}

	@Override
	protected ReactiveMongoTemplate createMongoTemplate(ReactiveMongoDatabaseFactory databaseFactory,
			MappingMongoConverter mappingMongoConverter, String tagSet) {
		var mongoTemplate = new ReactiveMongoTemplate(databaseFactory, mappingMongoConverter);

		List<TagSet> tagSets = MongoDBCluster.replicaTagSets(tagSet);
		var readPreference = CollectionUtils.isEmpty(tagSets) ? ReadPreference.secondaryPreferred()
				: ReadPreference.secondaryPreferred(tagSets);
		mongoTemplate.setReadPreference(readPreference);
		return mongoTemplate;
	}

	@Override
	public String toString() {
		return "ReactiveMongoDBConnector{} " + super.toString();
	}

}
