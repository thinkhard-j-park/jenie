package org.jenie.spring.test.data.mongodb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dbconn")
public class DBConn {

	@Id
	private String id;

	private String clusterKey;

	private String dbKey;

	private String dbName;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClusterKey() {
		return this.clusterKey;
	}

	public void setClusterKey(String clusterKey) {
		this.clusterKey = clusterKey;
	}

	public String getDbKey() {
		return this.dbKey;
	}

	public void setDbKey(String dbKey) {
		this.dbKey = dbKey;
	}

	public String getDbName() {
		return this.dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "DBConn{" +
				"id='" + this.id + '\'' +
				", clusterKey='" + this.clusterKey + '\'' +
				", dbKey='" + this.dbKey + '\'' +
				", dbName='" + this.dbName + '\'' +
				'}';
		//@formatter:on
	}

}
