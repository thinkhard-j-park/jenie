package org.jenie.spring.data.mongodb.connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.reactivestreams.client.MongoClient;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ReactiveMongoDBConnectorRegistry {

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
		for (var dbConnTemplate : getTemplatesList()) {
			var query = Query.query(Criteria.where("dbKey").is(key));
			return dbConnTemplate.findOne(query, DBConn.class);
		}
		throw new DBConnNotFoundException("DBConn must not be null: " + key);
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
