package org.jenie.spring.data.mongodb.operation;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;

public record MongoTemplateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {

	private String generateKey() {
		var sb = new StringBuilder(this.dbKey);
		sb.append("|");
		if (this.readPreference != null) {
			sb.append(this.readPreference.getName());
			if (this.readPreference instanceof TaggableReadPreference taggableReadPreference) {
				for (TagSet tagSet : taggableReadPreference.getTagSetList()) {
					for (Tag tag : tagSet) {
						sb.append(tag.getName()).append(tag.getValue());
					}
				}
			}
		}

		sb.append("|");
		if (this.writeConcern != null) {
			sb.append("_")
				.append(this.writeConcern.getWObject())
				.append("_")
				.append(this.writeConcern.getWTimeout(TimeUnit.MILLISECONDS));
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
		MongoTemplateKey mongoTemplateKey = (MongoTemplateKey) obj;

		return Objects.equals(generateKey(), mongoTemplateKey.generateKey());
	}

	@Override
	public int hashCode() {
		return Objects.hash(generateKey());
	}
}