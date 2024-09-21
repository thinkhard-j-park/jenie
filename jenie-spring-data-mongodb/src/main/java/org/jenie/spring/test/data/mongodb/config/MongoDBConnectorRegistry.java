package org.jenie.spring.test.data.mongodb.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

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

	public Map<String, MongoDBConnector> getConnectors() {
		return this.connectors;
	}

	public MappingMongoConverter getMappingMongoConverter(String clusterKey) {
		return this.connectors.get(clusterKey).getMappingMongoConverter();
	}

	public MongoDBConnector getConnector(String clusterKey) {
		return this.connectors.get(clusterKey);
	}
}
