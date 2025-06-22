package org.jenie.spring.data.mongodb.operation;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
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
			if (readPreference instanceof TaggableReadPreference taggableReadPreference) {
				for (TagSet tagSet : taggableReadPreference.getTagSetList()) {
					for (Tag tag : tagSet) {
						sb.append(tag.getName()).append(tag.getValue());
					}
				}
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
