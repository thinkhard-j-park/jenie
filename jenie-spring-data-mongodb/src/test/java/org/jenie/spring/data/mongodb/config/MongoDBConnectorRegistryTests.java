package org.jenie.spring.data.mongodb.config;

import java.util.List;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MongoDBConnectorRegistryTests {

	@Mock
	private MongoMappingContext mappingContext;

	@Mock
	private MongoCustomConversions customConversions;

	@Test
	void addAndGet() {
		// given
		var clusterKey = "cluster1";
		var cluster = new MongoDBCluster();
		cluster.setUser("test-user");
		cluster.setPassword("<PASSWORD>");
		cluster.setAuthDB("admin");
		cluster.setDatabaseName("dbconn");
		cluster.setTagSet("");
		cluster.setMaxConnecting(10);
		cluster.setMaxSize(100);
		cluster.setMinSize(10);
		cluster.setWriteConcernW("W1");
		cluster.setHosts(List.of("192.168.0.1:27017", "192.168.0.2:27017", "192.168.0.3:27017"));
		var connector = new MongoDBConnector(cluster, this.mappingContext, this.customConversions);
		var registry = new MongoDBConnectorRegistry();

		// when
		registry.addConnector(clusterKey, connector);

		// then
		assertThat(registry.getConnectors()).hasSize(1);
		var fetchedConnector = registry.getConnector(clusterKey);
		assertThat(fetchedConnector).isNotNull();
		assertThat(fetchedConnector.getCluster()).isEqualTo(cluster);

		assertThat(registry.getTemplatesList()).hasSize(1);
		MongoTemplate template = registry.getTemplate(clusterKey);
		assertThat(template).isNotNull();

		MongoClient client = registry.getClient(clusterKey);
		assertThat(client).isNotNull();

		MappingMongoConverter mongoConverter = registry.getMappingMongoConverter(clusterKey);
		assertThat(mongoConverter).isNotNull();
	}

}
