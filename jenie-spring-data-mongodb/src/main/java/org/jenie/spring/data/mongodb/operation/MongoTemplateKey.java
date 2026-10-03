package org.jenie.spring.data.mongodb.operation;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.mongodb.ReadPreference;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;

public record MongoTemplateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern, String key) {

	public MongoTemplateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		this(dbKey, readPreference, writeConcern, generateKey(dbKey, readPreference, writeConcern));
	}

	public static String generateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		var sb = new StringBuilder(dbKey);
		sb.append("|");
		if (readPreference != null) {
			sb.append(readPreference.getName());
			if (readPreference instanceof TaggableReadPreference taggableReadPreference
					&& !taggableReadPreference.getTagSetList().isEmpty()) {
				// Preserve tag set boundaries, order, and empty fallback entries in the
				// key.
				// [{"dc":"us"}, {}] has a different fallback policy from [{"dc":"us"}].
				sb.append(taggableReadPreference.toDocument().get("tags"));
			}
		}

		sb.append("|");
		if (writeConcern != null) {
			sb.append("_")
				.append(writeConcern.getWObject())
				.append("_")
				.append(writeConcern.getWTimeout(TimeUnit.MILLISECONDS));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		MongoTemplateKey other = (MongoTemplateKey) obj;
		return this.key.equals(other.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}

}
