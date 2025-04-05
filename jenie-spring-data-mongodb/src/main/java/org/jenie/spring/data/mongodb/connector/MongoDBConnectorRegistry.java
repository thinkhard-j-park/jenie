package org.jenie.spring.data.mongodb.connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

public class MongoDBConnectorRegistry {

	private final Map<String, MongoDBConnector> connectors = new HashMap<>();

	public void addConnector(String clusterKey, MongoDBConnector mongoDBConnector) {
		this.connectors.put(clusterKey, mongoDBConnector);
	}

	public MongoClient getClient(String clusterKey) {
		return this.connectors.get(clusterKey).getClient();
	}

	public MongoTemplate getTemplate(String clusterKey) {
		return this.connectors.get(clusterKey).getTemplate();
	}

	public List<MongoTemplate> getTemplatesList() {
		return this.connectors.values().stream().map(MongoDBConnector::getTemplate).toList();
	}

	public DBConn getDBConn(String key) {
		for (var dbConnTemplate : getTemplatesList()) {
			var query = Query.query(Criteria.where("dbKey").is(key));
			var dbConn = dbConnTemplate.findOne(query, DBConn.class);
			if (dbConn != null && StringUtils.hasText(dbConn.getId())) {
				return dbConn;
			}
		}
		throw new DBConnNotFoundException("DBConn must not be null: " + key);
	}

	public Map<String, MongoDBConnector> getConnectors() {
		return this.connectors;
	}

	public MappingMongoConverter getMappingMongoConverter(String clusterKey) {
		return this.connectors.get(clusterKey).getMappingMongoConverter();
	}

	public MongoDBConnector getConnector(String clusterKey) {
		return this.connectors.get(clusterKey);
	}

	@ExcludeCodeCoverageGenerated
	@Override
	public String toString() {
		//@formatter:off
		return "MongoDBConnectorRegistry{" +
				"connectors=" + this.connectors +
				'}';
		//@formatter:to
	}
}
