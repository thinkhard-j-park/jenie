package org.jenie.spring.data.mongodb.operation;

import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import org.jenie.spring.data.mongodb.config.MongoDBCluster;
import org.jenie.spring.data.mongodb.config.MongoDBConnector;
import org.jenie.spring.data.mongodb.config.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MongoTemplateRouterTests {

	@InjectMocks
	private MongoTemplateRouter mongoTemplateRouter;

	@Mock
	private MongoDBConnectorRegistry mongoDBConnectorRegistry;

	@Mock
	private MongoTemplate dbConnTemplate;

	@Mock
	private MongoDBConnector mongoDBConnector;

	@Mock
	private MongoClient mongoClient;

	@Test
	void getMongoTemplate() {
		// given
		var dbKey = "dbKey1";
		var mongoTemplateKey = new MongoTemplateKey(dbKey, null, null);
		var dbConn = new DBConn();
		dbConn.setDbKey(dbKey);
		dbConn.setClusterKey("test-cluster");
		dbConn.setDbName("test-db");
		dbConn.setId("mongodb-id");

		var mongoCluster = new MongoDBCluster();
		given(this.dbConnTemplate.findOne(any(Query.class), any())).willReturn(dbConn);
		given(this.mongoDBConnectorRegistry.getTemplatesList()).willReturn(List.of(this.dbConnTemplate));
		given(this.mongoDBConnectorRegistry.getConnector(dbConn.getClusterKey())).willReturn(this.mongoDBConnector);
		given(this.mongoDBConnector.getClient()).willReturn(this.mongoClient);
		given(this.mongoDBConnector.getCluster()).willReturn(mongoCluster);

		// when
		var template = this.mongoTemplateRouter.mongoTemplate(mongoTemplateKey.dbKey(),
				mongoTemplateKey.readPreference(), mongoTemplateKey.writeConcern());

		// then
		assertThat(template).isNotNull();
		assertThat(template.getMongoDatabaseFactory()).isNotNull();
		assertThat(template.getReadPreference()).isNull();
	}

	@Test
	void getMongoTemplateWithReadPreference() {
		// given
		var dbKey = "dbKey1";
		var secondaryPreferred = ReadPreference.secondaryPreferred();
		var mongoTemplateKey = new MongoTemplateKey(dbKey, secondaryPreferred, null);
		var dbConn = new DBConn();
		dbConn.setDbKey(dbKey);
		dbConn.setClusterKey("test-cluster");
		dbConn.setDbName("test-db");
		dbConn.setId("mongodb-id");

		var mongoCluster = new MongoDBCluster();
		given(this.dbConnTemplate.findOne(any(Query.class), any())).willReturn(dbConn);
		given(this.mongoDBConnectorRegistry.getTemplatesList()).willReturn(List.of(this.dbConnTemplate));
		given(this.mongoDBConnectorRegistry.getConnector(dbConn.getClusterKey())).willReturn(this.mongoDBConnector);
		given(this.mongoDBConnector.getClient()).willReturn(this.mongoClient);
		given(this.mongoDBConnector.getCluster()).willReturn(mongoCluster);

		// when
		var template = this.mongoTemplateRouter.mongoTemplate(mongoTemplateKey.dbKey(),
				mongoTemplateKey.readPreference(), mongoTemplateKey.writeConcern());

		// then
		assertThat(template).isNotNull();
		assertThat(template.getMongoDatabaseFactory()).isNotNull();
		assertThat(template.getReadPreference()).isNotNull();
		assertThat(template.getReadPreference().getName()).isEqualTo(ReadPreference.secondaryPreferred().getName());
	}

}
