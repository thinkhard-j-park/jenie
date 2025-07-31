package org.jenie.spring.data.mongodb.connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.mongodb.reactivestreams.client.MongoClient;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

public class ReactiveMongoDBConnectorRegistry {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveMongoDBConnectorRegistry.class);

	private final Map<String, ReactiveMongoDBConnector> connectors = new HashMap<>();

	public void addConnector(String clusterKey, ReactiveMongoDBConnector mongoDBConnector) {
		this.connectors.put(clusterKey, mongoDBConnector);
	}

	public MongoClient getClient(String clusterKey) {
		return this.connectors.get(clusterKey).getClient();
	}

	public ReactiveMongoTemplate getTemplate(String clusterKey) {
		return this.connectors.get(clusterKey).getTemplate();
	}

	public List<ReactiveMongoTemplate> getTemplatesList() {
		return this.connectors.values().stream().map(ReactiveMongoDBConnector::getTemplate).toList();
	}

	public Mono<DBConn> getDBConn(String key) {
		return Flux.fromIterable(getTemplatesList()).flatMap((dbConnTemplate) -> {
			var query = Query.query(Criteria.where("dbKey").is(key));
			return dbConnTemplate.findOne(query, DBConn.class);
		})
			.filter((dbConn) -> dbConn != null && StringUtils.hasText(dbConn.getId()))
			.single()
			.onErrorMap(NoSuchElementException.class,
					(noSuchElementException) -> new DBConnNotFoundException("DBConn not found: " + key))
			.onErrorMap(IndexOutOfBoundsException.class,
					(indexOutOfBoundsException) -> new DBConnNotFoundException("DBConn must be unique: " + key))
			.switchIfEmpty(Mono.error(new DBConnNotFoundException("DBConn not found: " + key)));
	}

	public Map<String, ReactiveMongoDBConnector> getConnectors() {
		return this.connectors;
	}

	public MappingMongoConverter getMappingMongoConverter(String clusterKey) {
		return this.connectors.get(clusterKey).getMappingMongoConverter();
	}

	public ReactiveMongoDBConnector getConnector(String clusterKey) {
		return this.connectors.get(clusterKey);
	}

	@Override
	public String toString() {
		//@formatter:off
		return "ReactiveMongoDBConnectorRegistry{" +
				"connectors=" + this.connectors +
				'}';
		//@formatter:on
	}

}
