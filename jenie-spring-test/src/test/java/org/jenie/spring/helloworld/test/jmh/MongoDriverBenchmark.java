package org.jenie.spring.helloworld.test.jmh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.client.MongoClients;
import org.bson.types.ObjectId;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
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

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Fork(10)
@Threads(1)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 100, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MongoDriverBenchmark {

	@Benchmark
	public void syncDriver(BenchmarkState state) {
		listArticlesSync(state);
		viewArticleSync(state);
		incSync(state);
	}

	public List<ArticleHeaderEntity> listArticlesSync(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		var criteria = Criteria.where("state").is(ArticleState.Normal.getCode()).and("_id").lt(id);
		var sortOrder = SortOrder.TIME_DESC;
		var sort = Sort.by(sortOrder.getDirection(), sortOrder.getField());
		var query = Query.query(criteria).with(sort).limit(20);

		return state.mongoTemplate.find(query, ArticleHeaderEntity.class);
	}

	public ArticleContentEntity viewArticleSync(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		return state.mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), ArticleContentEntity.class);
	}

	public ArticleHeaderEntity incSync(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		var query = Query.query(Criteria.where("_id").is(id));
		var update = new Update();
		update.inc("reaction.viewCount", 1);
		var option = FindAndModifyOptions.options().returnNew(true);

		return state.mongoTemplate.findAndModify(query, update, option, ArticleHeaderEntity.class);
	}

	@Benchmark
	public void reactiveDriver(BenchmarkState state) {
		listArticlesReactive(state);
		viewArticleReactive(state);
		incReactive(state);
	}

	public List<ArticleHeaderEntity> listArticlesReactive(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		var criteria = Criteria.where("state").is(ArticleState.Normal.getCode()).and("_id").lt(id);
		var sortOrder = SortOrder.TIME_DESC;
		var sort = Sort.by(sortOrder.getDirection(), sortOrder.getField());
		var query = Query.query(criteria).with(sort).limit(20);

		return state.reactiveMongoTemplate.find(query, ArticleHeaderEntity.class).collectList().block();
	}

	public ArticleContentEntity viewArticleReactive(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		return state.reactiveMongoTemplate
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleContentEntity.class)
			.block();
	}

	public ArticleHeaderEntity incReactive(BenchmarkState state) {
		int inx = state.random.nextInt(state.ids.size());
		var id = new ObjectId(state.ids.get(inx));

		var query = Query.query(Criteria.where("_id").is(id));
		var update = new Update();
		update.inc("reaction.viewCount", 1);
		var option = FindAndModifyOptions.options().returnNew(true);

		return state.reactiveMongoTemplate.findAndModify(query, update, option, ArticleHeaderEntity.class).block();
	}

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		private final Random random = new Random();

		List<String> ids;

		private MongoTemplate mongoTemplate;

		private ReactiveMongoTemplate reactiveMongoTemplate;

		private AtomicInteger counter;

		@Setup(Level.Trial)
		public void initialize() throws IOException {
			this.ids = Files.readAllLines(Paths.get("perf/jenie-hello-ids.txt"));
			String connectionString = "mongodb://jenie:PwWSTRnVzG@192.168.0.4:27017,192.168.0.11:27017,192.168.0.14:27017/?replicaSet=rs0&authSource=admin&readPreference=secondaryPreferred";
			var dbName = "jenie-hello";

			com.mongodb.client.MongoClient syncClient = MongoClients.create(connectionString);
			this.mongoTemplate = new MongoTemplate(syncClient, dbName);

			com.mongodb.reactivestreams.client.MongoClient reactiveClient = com.mongodb.reactivestreams.client.MongoClients
				.create(connectionString);
			this.reactiveMongoTemplate = new ReactiveMongoTemplate(reactiveClient, dbName);

			this.counter = new AtomicInteger(this.random.nextInt(this.ids.size()));
		}

	}

}
