package org.jenie.spring.helloworld.test.jmh;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnector;
import org.jenie.spring.data.mongodb.connector.ReactiveMongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.operation.ReactiveCaffeineMongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.jenie.spring.data.mongodb.operation.ReactiveSimpleMongoTemplateRouter;
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
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
public class ReactiveMongoTemplateRouterBenchmark {

	@Benchmark
	public ReactiveMongoTemplate reactiveSimpleCacheHit(BenchmarkState state) {
		return state.simpleRouter.mongoTemplate(state.CACHE_HIT_KEY, state.readPreference, state.writeConcern).block();
	}

	@Benchmark
	public ReactiveMongoTemplate reactiveCaffeineCacheHit(BenchmarkState state) {
		return state.caffeineRouter.mongoTemplate(state.CACHE_HIT_KEY, state.readPreference, state.writeConcern)
			.block();
	}

	@Benchmark
	public ReactiveMongoTemplate reactiveSimpleCacheMiss(BenchmarkState state, ThreadState threadState) {
		return state.simpleRouter.mongoTemplate(threadState.getNextKey(), state.readPreference, state.writeConcern)
			.block();
	}

	@Benchmark
	public ReactiveMongoTemplate reactiveCaffeineCacheMiss(BenchmarkState state, ThreadState threadState) {
		return state.caffeineRouter.mongoTemplate(threadState.getNextKey(), state.readPreference, state.writeConcern)
			.block();
	}

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		ReactiveMongoTemplateRouter simpleRouter;

		ReactiveMongoTemplateRouter caffeineRouter;

		final String CACHE_HIT_KEY = "test-db";

		final ReadPreference readPreference = ReadPreference.secondaryPreferred();

		final WriteConcern writeConcern = WriteConcern.W1;

		@Setup(Level.Trial)
		public void initialize() {
			var mockRegistry = mock(ReactiveMongoDBConnectorRegistry.class);
			var mockConnector = mock(ReactiveMongoDBConnector.class);
			var mockDbConn = mock(DBConn.class);
			var mockClient = mock(com.mongodb.reactivestreams.client.MongoClient.class);
			var mockCluster = mock(MongoDBCluster.class);

			var mappingContext = new MongoMappingContext();
			var mappingMongoConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);

			given(mockDbConn.getClusterKey()).willReturn("cluster1");
			given(mockDbConn.getDbName()).willReturn("test-db");
			given(mockDbConn.getDbKey()).willReturn("test-db");

			given(mockConnector.getClient()).willReturn(mockClient);
			given(mockConnector.getMappingMongoConverter()).willReturn(mappingMongoConverter);
			given(mockConnector.getCluster()).willReturn(mockCluster);

			given(mockCluster.getTagSet()).willReturn(null);

			given(mockRegistry.getDBConn(anyString())).willReturn(Mono.just(mockDbConn));
			given(mockRegistry.getConnector(anyString())).willReturn(mockConnector);

			this.simpleRouter = new ReactiveSimpleMongoTemplateRouter(mockRegistry);
			this.caffeineRouter = new ReactiveCaffeineMongoTemplateRouter(mockRegistry);
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
