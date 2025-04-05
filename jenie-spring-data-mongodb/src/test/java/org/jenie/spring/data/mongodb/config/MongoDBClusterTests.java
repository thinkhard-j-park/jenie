package org.jenie.spring.data.mongodb.config;

import java.util.List;

import com.mongodb.Tag;
import com.mongodb.TagSet;
import org.jenie.spring.data.mongodb.connector.MongoDBCluster;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MongoDBClusterTests {

	@Test
	void replicaSet() {
		var taSet = "dc:kr,dc:us,dc:eu";
		List<TagSet> tagSetList = MongoDBCluster.replicaTagSets(taSet);
		assertThat(tagSetList).hasSize(3);
		assertThat(tagSetList.get(0)).isEqualTo(new TagSet(List.of(new Tag("dc", "kr"))));
		assertThat(tagSetList.get(1)).isEqualTo(new TagSet(List.of(new Tag("dc", "us"))));
		assertThat(tagSetList.get(2)).isEqualTo(new TagSet(List.of(new Tag("dc", "eu"))));
	}

}
