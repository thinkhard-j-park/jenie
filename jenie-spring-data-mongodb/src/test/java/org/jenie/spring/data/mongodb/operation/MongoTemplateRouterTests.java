package org.jenie.spring.data.mongodb.operation;

import java.util.List;
import java.util.stream.Stream;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import org.jenie.spring.data.mongodb.config.MongoDBCluster;
import org.jenie.spring.data.mongodb.config.MongoDBConnector;
import org.jenie.spring.data.mongodb.config.MongoDBConnectorRegistry;
import org.jenie.spring.data.mongodb.domain.DBConn;
import org.jenie.spring.data.mongodb.exception.DBConnNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MongoTemplateRouterTests {

	@InjectMocks
	private MongoTemplateRouterCaffeine mongoTemplateRouter;

	@Mock
	private MongoDBConnectorRegistry mongoDBConnectorRegistry;

	@Mock
	private MongoTemplate dbConnTemplate;

	@Mock
	private MongoDBConnector mongoDBConnector;

	@Mock
	private MongoClient mongoClient;

	@Test
	void getDefaultMongoTemplate() {
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
		var template = this.mongoTemplateRouter.mongoTemplate(mongoTemplateKey.dbKey());

		// then
		assertThat(template).isNotNull();
		assertThat(template.getMongoDatabaseFactory()).isNotNull();
		assertThat(template.getReadPreference()).isEqualTo(ReadPreference.secondaryPreferred());
	}

	@Test
	void getMongoTemplateShouldFail() {
		// given
		var mongoTemplateKey = new MongoTemplateKey("unknown", null, null);

		given(this.dbConnTemplate.findOne(any(Query.class), any())).willReturn(null);
		given(this.mongoDBConnectorRegistry.getTemplatesList()).willReturn(List.of(this.dbConnTemplate));

		// when, then
		assertThatThrownBy(() -> this.mongoTemplateRouter.mongoTemplate(mongoTemplateKey.dbKey()))
			.isInstanceOf(DBConnNotFoundException.class);
	}

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

	static Stream<Arguments> provideReadPreferenceWriteConcern() {
		//@formatter:off
		return Stream.of(
				Arguments.of(ReadPreference.secondaryPreferred(), WriteConcern.W1),
				Arguments.of(ReadPreference.primary(), WriteConcern.MAJORITY),
				Arguments.of(ReadPreference.primary(), null));
		//@formatter:on
	}

	@ParameterizedTest
	@MethodSource("provideReadPreferenceWriteConcern")
	void getMongoTemplateWithReadPreferenceWriteConcern(ReadPreference readPreference, WriteConcern writeConcern) {
		// given
		var dbKey = "dbKey1";
		var mongoTemplateKey = new MongoTemplateKey(dbKey, readPreference, writeConcern);
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
		assertThat(template.getReadPreference()).isEqualTo(readPreference);
	}

	@Test
	void getMongoTemplateWithReadPreferenceTagSet1() {
		// given
		var tagSet = new TagSet(List.of(new Tag("region", "asia")));
		var readPreference = ReadPreference.secondaryPreferred().withTagSetList(List.of(tagSet));
		var dbKey = "dbKey1";
		var mongoTemplateKey = new MongoTemplateKey(dbKey, readPreference, null);
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
		assertThat(template.getReadPreference()).isEqualTo(readPreference);
		var tagSets = readPreference.toDocument().get("tags");
		assertThat(tagSets.asArray()).hasSize(1);
		assertThat(tagSets.asArray().getFirst().asDocument().get("region").asString().getValue()).isEqualTo("asia");
	}

	@Test
	void getMongoTemplateWithReadPreferenceTagSet2() {
		// given
		var tagSet = new TagSet(List.of(new Tag("region", "asia"), new Tag("dc", "kr")));
		var readPreference = ReadPreference.secondaryPreferred().withTagSetList(List.of(tagSet));
		var dbKey = "dbKey1";
		var mongoTemplateKey = new MongoTemplateKey(dbKey, readPreference, null);
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
		assertThat(template.getReadPreference()).isEqualTo(readPreference);
		var tagSets = readPreference.toDocument().get("tags");
		assertThat(tagSets.asArray()).hasSize(1);
		assertThat(tagSets.asArray().getFirst().asDocument().get("region").asString().getValue()).isEqualTo("asia");
		assertThat(tagSets.asArray().getFirst().asDocument().get("dc").asString().getValue()).isEqualTo("kr");
	}

	@Test
	void getMongoTemplateWithReadPreferenceTagSet3() {
		// given
		var tagSet1 = new TagSet(List.of(new Tag("dc", "kr")));
		var tagSet2 = new TagSet(List.of(new Tag("dc", "us")));
		var readPreference = ReadPreference.secondaryPreferred().withTagSetList(List.of(tagSet1, tagSet2));
		var dbKey = "dbKey1";
		var mongoTemplateKey = new MongoTemplateKey(dbKey, readPreference, null);
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
		assertThat(template.getReadPreference()).isEqualTo(readPreference);
		var tagSets = readPreference.toDocument().get("tags");
		assertThat(tagSets.asArray()).hasSize(2);
		assertThat(tagSets.asArray().getFirst().asDocument().get("dc").asString().getValue()).isEqualTo("kr");
		assertThat(tagSets.asArray().getLast().asDocument().get("dc").asString().getValue()).isEqualTo("us");
	}

	@Test
	void getTransactionManager() {
		// given
		var dbKey = "dbKey1";
		var dbConn = new DBConn();
		dbConn.setDbKey(dbKey);
		dbConn.setClusterKey("test-cluster");
		dbConn.setDbName("test-db");
		dbConn.setId("mongodb-id");

		given(this.dbConnTemplate.findOne(any(Query.class), any())).willReturn(dbConn);
		given(this.mongoDBConnectorRegistry.getTemplatesList()).willReturn(List.of(this.dbConnTemplate));
		given(this.mongoDBConnectorRegistry.getConnector(dbConn.getClusterKey())).willReturn(this.mongoDBConnector);
		given(this.mongoDBConnector.getClient()).willReturn(this.mongoClient);

		// when
		var transactionManager = this.mongoTemplateRouter.transactionManager(dbKey);

		// then
		assertThat(transactionManager).isNotNull();
		assertThat(transactionManager.getDatabaseFactory()).isNotNull();
	}

}
