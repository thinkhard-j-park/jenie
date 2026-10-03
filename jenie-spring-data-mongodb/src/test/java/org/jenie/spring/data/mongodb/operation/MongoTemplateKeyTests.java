package org.jenie.spring.data.mongodb.operation;

import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoTemplateKeyTests {

	@Test
	void compareMongoTemplateKey() {
		var key1 = new MongoTemplateKey("k1", null, null);
		assertThat(key1).isNotEqualTo(null);
		assertThat(key1).isNotEqualTo(String.class);

		var key2 = new MongoTemplateKey("k1", null, null);
		assertThat(key1).isEqualTo(key2);

		var key3 = new MongoTemplateKey("k3", null, null);
		var key4 = new MongoTemplateKey("k4", null, null);
		assertThat(key3).isNotEqualTo(key4);
	}

	@Test
	void compareMongoTemplateKeyWithWriteConcern() {
		var key1 = new MongoTemplateKey("k1", null, WriteConcern.W1);
		var key2 = new MongoTemplateKey("k1", null, WriteConcern.W1);
		assertThat(key1).isEqualTo(key2);

		var key3 = new MongoTemplateKey("k3", null, WriteConcern.W1);
		var key4 = new MongoTemplateKey("k4", null, WriteConcern.W3);
		assertThat(key3).isNotEqualTo(key4);
	}

	@Test
	void compareMongoTemplateKeyWithReadPreference() {
		var key1 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(), null);
		var key2 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(), null);
		assertThat(key1).isEqualTo(key2);

		var key3 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(), null);
		var key4 = new MongoTemplateKey("k1", ReadPreference.nearest(), null);
		assertThat(key3).isNotEqualTo(key4);
	}

	@Test
	void compareMongoTemplateKeyWithTaggableReadPreference() {
		var tagSet1 = new TagSet(List.of(new Tag("dc", "kr")));
		var tagSet2 = new TagSet(List.of(new Tag("dc", "kr")));
		var key1 = new MongoTemplateKey("k1", TaggableReadPreference.secondary(tagSet1), null);
		var key2 = new MongoTemplateKey("k1", TaggableReadPreference.secondary(tagSet2), null);
		assertThat(key1).isEqualTo(key2);

		var tagSet3 = new TagSet(List.of(new Tag("dc", "kr")));
		var tagSet4 = new TagSet(List.of(new Tag("dc", "us")));
		var key3 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(tagSet3), null);
		var key4 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(tagSet4), null);
		var key5 = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(), null);
		assertThat(key3).isNotEqualTo(key4);
		assertThat(key4).isNotEqualTo(key5);
	}

	@Test
	void distinguishExplicitEmptyTagSetInMongoTemplateKey() {
		var withoutTags = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(), null);
		var withEmptyTag = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(List.of(new TagSet())), null);
		assertThat(withEmptyTag).isNotEqualTo(withoutTags);
	}

	@Test
	void distinguishReadPreferenceWithFallbackTagSetInMongoTemplateKey() {
		// With secondaryPreferred, [{"dc":"us"}] selects the primary when no eligible
		// US secondary exists. [{"dc":"us"}, {}] can select another eligible secondary
		// first, so these requests must not share a cached template.
		var tag = new TagSet(new Tag("dc", "us"));
		var withTag = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(List.of(tag)), null);
		var withFallback = new MongoTemplateKey("k1", ReadPreference.secondaryPreferred(List.of(tag, new TagSet())),
				null);
		assertThat(withFallback).isNotEqualTo(withTag);
	}

}
