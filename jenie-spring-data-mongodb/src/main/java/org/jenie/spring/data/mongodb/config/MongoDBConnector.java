package org.jenie.spring.data.mongodb.config;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.TagSet;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class MongoDBConnector {

	private final MongoDBCluster cluster;

	private final MongoCustomConversions customConversions;

	private final MongoMappingContext mappingContext;

	private MongoClient client;

	private MappingMongoConverter mappingMongoConverter;

	private MongoTemplate template;

	public MongoDBConnector(MongoDBCluster cluster, MongoMappingContext mappingContext,
			MongoCustomConversions customConversions) {
		this.cluster = cluster;
		this.customConversions = customConversions;
		this.mappingContext = mappingContext;
		configureMongoConnector();
	}

	protected void configureMongoConnector() {
		this.client = createMongoClients(this.cluster);
		var databaseFactory = new SimpleMongoClientDatabaseFactory(this.client, this.cluster.getDatabaseName());
		this.mappingMongoConverter = createMongoConverter(this.mappingContext, this.customConversions, databaseFactory);
		this.template = createMongoTemplate(databaseFactory, this.mappingMongoConverter, this.cluster.getTagSet());
	}

	protected MongoClient createMongoClients(MongoDBCluster conn) {

		var builder = MongoClientSettings.builder();

		builder.applyToClusterSettings((settings) -> {
			var hosts = conn.getHosts().stream().map((host) -> {
				var serverPort = host.split(":");
				return new ServerAddress(serverPort[0].trim(), Integer.parseInt(serverPort[1].trim()));
			}).collect(Collectors.toList());
			settings.hosts(hosts);
		});

		builder.applyToConnectionPoolSettings((settings) -> settings.maxConnecting(conn.getMaxConnecting())
			.maxConnectionLifeTime(conn.getMaxConnectionLifeTimeMS(), TimeUnit.MILLISECONDS)
			.minSize(conn.getMinSize())
			.maxSize(conn.getMaxSize())
			.maintenanceFrequency(conn.getMaintenanceFrequencyMS(), TimeUnit.MILLISECONDS)
			.maintenanceInitialDelay(conn.getMaintenanceInitialDelayMS(), TimeUnit.MILLISECONDS)
			.maxConnectionIdleTime(conn.getMaxConnectionIdleTimeMS(), TimeUnit.MILLISECONDS)
			.maxWaitTime(conn.getMaxWaitTimeMS(), TimeUnit.MILLISECONDS));

		if (StringUtils.hasText(conn.getUser()) && StringUtils.hasText(conn.getPassword())) {
			builder.credential(MongoCredential.createCredential(conn.getUser(), conn.getAuthDB(),
					conn.getPassword().toCharArray()));
		}

		builder.readConcern(new ReadConcern(ReadConcernLevel.fromString(conn.getReadConcernLevel())));
		builder.writeConcern(WriteConcern.valueOf(conn.getWriteConcernW())
			.withJournal(conn.isWriteConcernJournal())
			.withWTimeout(conn.getWriteConcernWTimeout(), TimeUnit.SECONDS));

		builder.codecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));

		builder.applicationName(conn.getAppName());

		MongoClientSettings clientSettings = builder.build();

		return MongoClients.create(clientSettings);
	}

	protected MappingMongoConverter createMongoConverter(MongoMappingContext mappingContext,
			MongoCustomConversions customConversions, SimpleMongoClientDatabaseFactory databaseFactory) {

		var mappingMongoConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
		mappingMongoConverter.setCustomConversions(customConversions);
		mappingMongoConverter.setCodecRegistryProvider(databaseFactory);
		mappingMongoConverter.afterPropertiesSet();
		return mappingMongoConverter;

	}

	protected MongoTemplate createMongoTemplate(SimpleMongoClientDatabaseFactory databaseFactory,
			MappingMongoConverter mappingMongoConverter, String tagSet) {
		var mongoTemplate = new MongoTemplate(databaseFactory, mappingMongoConverter);
		List<TagSet> tagSets = MongoDBCluster.replicaTagSets(tagSet);
		var readPreference = CollectionUtils.isEmpty(tagSets) ? ReadPreference.secondaryPreferred()
				: ReadPreference.secondaryPreferred(tagSets);
		mongoTemplate.setReadPreference(readPreference);
		return mongoTemplate;
	}

	public MongoDBCluster getCluster() {
		return this.cluster;
	}

	public MongoCustomConversions getCustomConversions() {
		return this.customConversions;
	}

	public MongoMappingContext getMappingContext() {
		return this.mappingContext;
	}

	public MongoClient getClient() {
		return this.client;
	}

	public MappingMongoConverter getMappingMongoConverter() {
		return this.mappingMongoConverter;
	}

	public MongoTemplate getTemplate() {
		return this.template;
	}

	@ExcludeCodeCoverageGenerated
	@Override
	public String toString() {
		//@formatter:off
		return "MongoDBConnector{" +
				"client=" + this.client +
				", cluster=" + this.cluster +
				", customConversions=" + this.customConversions +
				", mappingContext=" + this.mappingContext +
				", mappingMongoConverter=" + this.mappingMongoConverter +
				", template=" + this.template +
				'}';
		//@formatter:on
	}

}
