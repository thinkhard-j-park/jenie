package org.jenie.spring.data.mongodb.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.jenie.spring.data.mongodb.connector.MongoDBSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoDBConfigTests {

	private MongoDBConfig mongoDBConfig;

	@Mock
	private MongoMappingContext mappingContext;

	@Mock
	private MongoCustomConversions customConversions;

	@Mock
	private MongoCustomConversions.MongoConverterConfigurationAdapter converterConfigurationAdapter;

	@BeforeEach
	void setUp() {
		this.mongoDBConfig = new MongoDBConfig() {
			@Override
			protected String getDatabaseName() {
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

	@Test
	void configureConverters() {
		// when
		this.mongoDBConfig.configureConverters(this.converterConfigurationAdapter);

		// then
		verify(this.converterConfigurationAdapter)
			.registerConverter(any(CustomConverter.ZonedDateTimeReadConverter.class));
		verify(this.converterConfigurationAdapter)
			.registerConverter(any(CustomConverter.ZonedDateTimeWriteConverter.class));

	}

	@Test
	void zonedDataTimeReadConverter() {
		var zdt = ZonedDateTime.of(2024, 10, 27, 12, 1, 2, 0, ZoneId.systemDefault());
		var source = Date.from(zdt.toInstant());
		var converter = new CustomConverter.ZonedDateTimeReadConverter();
		ZonedDateTime converted = converter.convert(source);
		assertThat(converted).isEqualTo(zdt);
	}

	@Test
	void zonedDataTimeWriteConverter() {
		var source = ZonedDateTime.of(2024, 10, 27, 12, 1, 2, 0, ZoneId.systemDefault());
		var converter = new CustomConverter.ZonedDateTimeWriteConverter();
		Date converted = converter.convert(source);
		assertThat(converted).isNotNull();
		assertThat(converted.toInstant()).isEqualTo(source.toInstant());
	}

}
