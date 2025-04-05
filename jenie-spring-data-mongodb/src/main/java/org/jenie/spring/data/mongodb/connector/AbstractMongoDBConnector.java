package org.jenie.spring.data.mongodb.connector;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import org.springframework.data.mongodb.CodecRegistryProvider;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.StringUtils;

public abstract class AbstractMongoDBConnector<C, DF extends CodecRegistryProvider, T> {

	private final MongoDBCluster cluster;

	private final MongoCustomConversions customConversions;

	private final MongoMappingContext mappingContext;

	private C client;

	private MappingMongoConverter mappingMongoConverter;

	private T template;

	AbstractMongoDBConnector(MongoDBCluster cluster, MongoMappingContext mappingContext,
			MongoCustomConversions customConversions) {
		this.cluster = cluster;
		this.customConversions = customConversions;
		this.mappingContext = mappingContext;
		configureMongoConnector();
	}

	protected void configureMongoConnector() {
		this.client = createMongoClients(this.cluster);
		var databaseFactory = mongoDatabaseFactory(this.client, this.cluster.getDatabaseName());
		this.mappingMongoConverter = createMongoConverter(this.mappingContext, this.customConversions, databaseFactory);
		this.template = createMongoTemplate(databaseFactory, this.mappingMongoConverter, this.cluster.getTagSet());
	}

	protected abstract C createMongoClients(MongoDBCluster conn);

	protected abstract DF mongoDatabaseFactory(C client, String databaseName);

	protected MappingMongoConverter createMongoConverter(MongoMappingContext mappingContext,
			MongoCustomConversions customConversions, CodecRegistryProvider codecRegistryProvider) {

		var mappingMongoConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
		mappingMongoConverter.setCustomConversions(customConversions);
		mappingMongoConverter.setCodecRegistryProvider(codecRegistryProvider);
		mappingMongoConverter.afterPropertiesSet();
		return mappingMongoConverter;
	}

	protected abstract T createMongoTemplate(DF databaseFactory, MappingMongoConverter mappingMongoConverter,
			String tagSet);

	protected MongoClientSettings mongoClientSettings(MongoDBCluster conn) {
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

		return builder.build();
	}

	public MongoDBCluster getCluster() {
		return this.cluster;
	}

	public C getClient() {
		return this.client;
	}

	public MappingMongoConverter getMappingMongoConverter() {
		return this.mappingMongoConverter;
	}

	public T getTemplate() {
		return this.template;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "AbstractMongoDBConnector{" +
				"cluster=" + this.cluster +
				", customConversions=" + this.customConversions +
				", mappingContext=" + this.mappingContext +
				", client=" + this.client +
				", mappingMongoConverter=" + this.mappingMongoConverter +
				", template=" + this.template +
				'}';
		//@formatter:on
	}

}
