package org.jenie.spring.helloworld.test.jmh;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.MongoDBConnector;
import org.jenie.spring.data.mongodb.connector.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.operation.CaffeineMongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.SimpleMongoTemplateRouter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Fork(4)
@Threads(16)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MongoTemplateRouterBenchmark {

	@Benchmark
	public MongoTemplate simpleCacheHit(BenchmarkState state) {
		return state.simpleRouter.mongoTemplate(state.CACHE_HIT_KEY, state.readPreference, state.writeConcern);
	}

	@Benchmark
	public MongoTemplate caffeineCacheHit(BenchmarkState state) {
		return state.caffeineRouter.mongoTemplate(state.CACHE_HIT_KEY, state.readPreference, state.writeConcern);
	}

	@Benchmark
	public MongoTemplate simpleCacheMiss(BenchmarkState state, ThreadState threadState) {
		return state.simpleRouter.mongoTemplate(threadState.getNextKey(), state.readPreference, state.writeConcern);
	}

	@Benchmark
	public MongoTemplate caffeineCacheMiss(BenchmarkState state, ThreadState threadState) {
		return state.caffeineRouter.mongoTemplate(threadState.getNextKey(), state.readPreference, state.writeConcern);
	}

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		MongoTemplateRouter simpleRouter;

		MongoTemplateRouter caffeineRouter;

		final String CACHE_HIT_KEY = "test-db";

		final ReadPreference readPreference = ReadPreference.secondaryPreferred();

		final WriteConcern writeConcern = WriteConcern.W1;

		@Setup(Level.Trial)
		public void initialize() {
			var mockRegistry = mock(MongoDBConnectorRegistry.class);
			var mockConnector = mock(MongoDBConnector.class);
			var mockDbConn = mock(DBConn.class);
			var mockClient = mock(MongoClient.class);
			var mockCluster = mock(MongoDBCluster.class);

			var mappingContext = new MongoMappingContext();
			var mappingMongoConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);

			given(mockDbConn.getClusterKey()).willReturn("cluster1");
			given(mockDbConn.getDbName()).willReturn("test-db");
			given(mockDbConn.getDbKey()).willReturn("testDB");

			given(mockConnector.getClient()).willReturn(mockClient);
			given(mockConnector.getMappingMongoConverter()).willReturn(mappingMongoConverter);
			given(mockConnector.getCluster()).willReturn(mockCluster);

			given(mockCluster.getTagSet()).willReturn(null);

			given(mockRegistry.getDBConn(anyString())).willReturn(mockDbConn);
			given(mockRegistry.getConnector(anyString())).willReturn(mockConnector);

			this.simpleRouter = new SimpleMongoTemplateRouter(mockRegistry);
			this.caffeineRouter = new CaffeineMongoTemplateRouter(mockRegistry);
		}

	}

	@State(Scope.Thread)
	public static class ThreadState {

		private final AtomicInteger counter = new AtomicInteger(0);

		public String getNextKey() {
			return "dbKey-" + this.counter.getAndIncrement();
		}

	}

}
