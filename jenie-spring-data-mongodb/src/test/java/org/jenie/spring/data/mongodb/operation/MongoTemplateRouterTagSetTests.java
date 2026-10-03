package org.jenie.spring.data.mongodb.operation;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.MongoDBConnector;
import org.jenie.spring.data.mongodb.connector.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnector;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MongoTemplateRouterTagSetTests {

	static Stream<Arguments> readPreferenceScenarios() {
		var clusterTag = new TagSet(new Tag("dc", "kr"));
		var requestTag = new TagSet(new Tag("dc", "us"));
		var fallbackTag = new TagSet();
		return Arrays.stream(RouterType.values())
			.flatMap((routerType) -> Stream.of(
					Arguments.of(routerType, "dc:kr", ReadPreference.secondaryPreferred(),
							ReadPreference.secondaryPreferred(List.of(clusterTag))),
					Arguments.of(routerType, "dc:kr",
							ReadPreference.secondaryPreferred(List.of(requestTag, fallbackTag)),
							ReadPreference.secondaryPreferred(List.of(requestTag, fallbackTag))),
					Arguments.of(routerType, "dc:kr", ReadPreference.secondaryPreferred(List.of(fallbackTag)),
							ReadPreference.secondaryPreferred(List.of(fallbackTag))),
					Arguments.of(routerType, null, ReadPreference.secondaryPreferred(List.of(requestTag)),
							ReadPreference.secondaryPreferred(List.of(requestTag))),
					Arguments.of(routerType, "dc:kr", ReadPreference.primary(), ReadPreference.primary())));
	}

	@ParameterizedTest(name = "{0}: cluster tags = {1}, read preference = {2}")
	@MethodSource("readPreferenceScenarios")
	void usesRequestTagsBeforeClusterDefaults(RouterType routerType, String clusterTags, ReadPreference readPreference,
			ReadPreference expectedReadPreference) {
		// given
		var originalReadPreference = readPreference.toDocument();
		var dbConn = new DBConn();
		dbConn.setDbKey("test-db");
		dbConn.setClusterKey("test-cluster");
		dbConn.setDbName("test-db");
		var cluster = new MongoDBCluster();
		cluster.setTagSet(clusterTags);
		var route = readPreferenceRouter(routerType, dbConn, cluster);
		// Populate the cache with cluster defaults before applying request-specific tags.
		route.apply(ReadPreference.secondaryPreferred());

		// when
		var actualReadPreference = route.apply(readPreference);

		// then
		assertThat(actualReadPreference).isEqualTo(expectedReadPreference);
		assertThat(readPreference.toDocument()).isEqualTo(originalReadPreference);
	}

	static Stream<Arguments> fallbackCacheScenarios() {
		return Arrays.stream(RouterType.values())
			.flatMap((routerType) -> Stream.of(Arguments.of(routerType, false), Arguments.of(routerType, true)));
	}

	@ParameterizedTest(name = "{0}: cache fallback request first = {1}")
	@MethodSource("fallbackCacheScenarios")
	void keepsFallbackReadPreferenceSeparateInCache(RouterType routerType, boolean fallbackFirst) {
		// given
		var tag = new TagSet(new Tag("dc", "us"));
		var withoutFallback = ReadPreference.secondaryPreferred(List.of(tag));
		var withFallback = ReadPreference.secondaryPreferred(List.of(tag, new TagSet()));
		var dbConn = new DBConn();
		dbConn.setDbKey("test-db");
		dbConn.setClusterKey("test-cluster");
		dbConn.setDbName("test-db");
		var cluster = new MongoDBCluster();
		cluster.setTagSet("dc:kr");
		var route = readPreferenceRouter(routerType, dbConn, cluster);
		var first = (fallbackFirst) ? withFallback : withoutFallback;
		var second = (fallbackFirst) ? withoutFallback : withFallback;

		// {} permits other eligible secondaries when none match dc:us; without it,
		// secondaryPreferred falls back to the primary. Preserve both configurations
		// regardless of cache population order, including when the first is read again.
		assertThat(route.apply(first)).isEqualTo(first);
		assertThat(route.apply(second)).isEqualTo(second);
		assertThat(route.apply(first)).isEqualTo(first);
	}

	private Function<ReadPreference, ReadPreference> readPreferenceRouter(RouterType routerType, DBConn dbConn,
			MongoDBCluster cluster) {
		return switch (routerType) {
			case SIMPLE, CAFFEINE -> {
				var registry = mock(MongoDBConnectorRegistry.class);
				var connector = mock(MongoDBConnector.class);
				given(registry.getDBConn(dbConn.getDbKey())).willReturn(dbConn);
				given(registry.getConnector(dbConn.getClusterKey())).willReturn(connector);
				given(connector.getClient()).willReturn(mock(MongoClient.class));
				given(connector.getCluster()).willReturn(cluster);
				MongoTemplateRouter router = (routerType == RouterType.SIMPLE) ? new SimpleMongoTemplateRouter(registry)
						: new CaffeineMongoTemplateRouter(registry);
				yield (readPreference) -> router.mongoTemplate(dbConn.getDbKey(), readPreference, null)
					.getReadPreference();
			}
			case REACTIVE_SIMPLE, REACTIVE_CAFFEINE -> {
				var registry = mock(ReactiveMongoDBConnectorRegistry.class);
				var connector = mock(ReactiveMongoDBConnector.class);
				var client = mock(com.mongodb.reactivestreams.client.MongoClient.class);
				var database = mock(MongoDatabase.class);
				@SuppressWarnings("unchecked")
				MongoCollection<Document> collection = mock(MongoCollection.class);
				given(registry.getDBConn(dbConn.getDbKey())).willReturn(Mono.just(dbConn));
				given(registry.getConnector(dbConn.getClusterKey())).willReturn(connector);
				given(connector.getClient()).willReturn(client);
				given(connector.getCluster()).willReturn(cluster);
				given(client.getDatabase(dbConn.getDbName())).willReturn(database);
				given(database.getCollection("test-collection", Document.class)).willReturn(collection);
				given(collection.withReadPreference(any(ReadPreference.class))).willReturn(collection);
				ReactiveMongoTemplateRouter router = (routerType == RouterType.REACTIVE_SIMPLE)
						? new ReactiveSimpleMongoTemplateRouter(registry)
						: new ReactiveCaffeineMongoTemplateRouter(registry);
				yield (readPreference) -> {
					var template = router.mongoTemplate(dbConn.getDbKey(), readPreference, null)
						.block(Duration.ofSeconds(5));
					assertThat(template).isNotNull();
					template.execute("test-collection", (preparedCollection) -> Mono.empty())
						.blockLast(Duration.ofSeconds(5));
					var captor = ArgumentCaptor.forClass(ReadPreference.class);
					verify(collection, atLeastOnce()).withReadPreference(captor.capture());
					return captor.getValue();
				};
			}
		};
	}

	enum RouterType {

		SIMPLE, CAFFEINE, REACTIVE_SIMPLE, REACTIVE_CAFFEINE

	}

}
