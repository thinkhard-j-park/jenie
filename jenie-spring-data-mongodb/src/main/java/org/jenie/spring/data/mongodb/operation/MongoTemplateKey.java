package org.jenie.spring.data.mongodb.operation;

import java.util.concurrent.TimeUnit;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.TaggableReadPreference;
import com.mongodb.WriteConcern;

public record MongoTemplateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern, String key) {

	public MongoTemplateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
		this(dbKey, null, null, generateKey(dbKey, readPreference, writeConcern));
	}

	private static String generateKey(String dbKey, ReadPreference readPreference, WriteConcern writeConcern) {
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

}
