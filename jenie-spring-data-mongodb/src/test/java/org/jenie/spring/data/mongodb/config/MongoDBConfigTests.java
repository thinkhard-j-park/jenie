package org.jenie.spring.data.mongodb.config;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MongoDBConfigTests {

	private MongoDBConfig mongoDBConfig;

	@Mock
	private MongoMappingContext mappingContext;

	@Mock
	private MongoCustomConversions customConversions;

	@BeforeEach
	void setUp() {
		this.mongoDBConfig = new MongoDBConfig() {
			@Override
			protected @NotNull String getDatabaseName() {
				return "dbconn";
			}
		};
	}

	@Test
	void mongoDBConnectorRegistry() {
		// given
		var clusterKey = "cluster1";
		var appName = "appName";

		var cluster = new MongoDBCluster();
		cluster.setAppName(appName);
		cluster.setHosts(List.of("192.168.0.1:27017", "192.168.0.2:27017", "192.168.0.3:27017"));

		var setting = new MongoDBSetting();
		setting.setAppName(appName);
		setting.getCluster().put(clusterKey, cluster);

		// when
		var registry = this.mongoDBConfig.mongoDBConnectorRegistry(setting, this.mappingContext,
				this.customConversions);

		// then
		assertThat(registry).isNotNull();
		assertThat(registry.getConnector(clusterKey)).isNotNull();
		assertThat(registry.getConnector(clusterKey).getCluster()).isNotNull();
		assertThat(registry.getConnector(clusterKey).getCluster().getAppName()).isEqualTo(setting.getAppName());
	}

}
