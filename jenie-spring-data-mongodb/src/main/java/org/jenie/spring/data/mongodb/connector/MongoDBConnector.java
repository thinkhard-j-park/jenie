package org.jenie.spring.data.mongodb.connector;

import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.TagSet;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.CollectionUtils;

public class MongoDBConnector extends AbstractMongoDBConnector<MongoClient, MongoDatabaseFactory, MongoTemplate> {

	public MongoDBConnector(MongoDBCluster cluster, MongoMappingContext mappingContext,
			MongoCustomConversions customConversions) {
		super(cluster, mappingContext, customConversions);
	}

	@Override
	protected MongoClient createMongoClients(MongoDBCluster conn) {
		var clientSettings = mongoClientSettings(conn);
		return MongoClients.create(clientSettings);
	}

	@Override
	protected MongoDatabaseFactory mongoDatabaseFactory(MongoClient client, String databaseName) {
		return new SimpleMongoClientDatabaseFactory(client, databaseName);
	}

	protected MongoTemplate createMongoTemplate(MongoDatabaseFactory databaseFactory,
			MappingMongoConverter mappingMongoConverter, String tagSet) {
		var mongoTemplate = new MongoTemplate(databaseFactory, mappingMongoConverter);
		List<TagSet> tagSets = MongoDBCluster.replicaTagSets(tagSet);
		var readPreference = CollectionUtils.isEmpty(tagSets) ? ReadPreference.secondaryPreferred()
				: ReadPreference.secondaryPreferred(tagSets);
		mongoTemplate.setReadPreference(readPreference);
		return mongoTemplate;
	}

	@Override
	public String toString() {
		return "MongoDBConnector{} " + super.toString();
	}

}
