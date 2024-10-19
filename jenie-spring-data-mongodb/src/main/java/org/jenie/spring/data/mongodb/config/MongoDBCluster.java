package org.jenie.spring.data.mongodb.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.WriteConcern;
import org.jenie.spring.util.ExcludeCodeCoverageGenerated;

import org.springframework.util.StringUtils;

public class MongoDBCluster {

	private String authDB = "admin";

	private List<String> hosts;

	private String user;

	private String password;

	private String databaseName = "dbconn";

	private String appName;

	private String tagSet;

	private int maxSize = 100;

	private int minSize = 0;

	private long maxWaitTimeMS = 1000 * 60 * 2;

	private long maxConnectionLifeTimeMS = 0;

	private long maxConnectionIdleTimeMS = 0;

	private long maintenanceInitialDelayMS = 0;

	private long maintenanceFrequencyMS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

	private int maxConnecting = 2;

	private String readConcernLevel = "local";

	private String writeConcernW = "W1";

	private long writeConcernWTimeout = 1L;

	private boolean writeConcernJournal = false;

	public static List<TagSet> replicaTagSets(String tagSet) {
		if (StringUtils.hasText(tagSet)) {
			return Arrays.stream(tagSet.split(","))
				.map((t) -> t.split(":"))
				.map((kv) -> new Tag(kv[0], kv[1]))
				.map(TagSet::new)
				.toList();
		}
		return Collections.emptyList();
	}

	public WriteConcern writeConcern() {
		return WriteConcern.valueOf(this.getWriteConcernW())
			.withJournal(this.isWriteConcernJournal())
			.withWTimeout(this.getWriteConcernWTimeout(), TimeUnit.SECONDS);
	}

	public String getAuthDB() {
		return this.authDB;
	}

	public void setAuthDB(String authDB) {
		this.authDB = authDB;
	}

	public List<String> getHosts() {
		return this.hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTagSet() {
		return this.tagSet;
	}

	public void setTagSet(String tagSet) {
		this.tagSet = tagSet;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMinSize() {
		return this.minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public long getMaxWaitTimeMS() {
		return this.maxWaitTimeMS;
	}

	public void setMaxWaitTimeMS(long maxWaitTimeMS) {
		this.maxWaitTimeMS = maxWaitTimeMS;
	}

	public long getMaxConnectionLifeTimeMS() {
		return this.maxConnectionLifeTimeMS;
	}

	public void setMaxConnectionLifeTimeMS(long maxConnectionLifeTimeMS) {
		this.maxConnectionLifeTimeMS = maxConnectionLifeTimeMS;
	}

	public long getMaxConnectionIdleTimeMS() {
		return this.maxConnectionIdleTimeMS;
	}

	public void setMaxConnectionIdleTimeMS(long maxConnectionIdleTimeMS) {
		this.maxConnectionIdleTimeMS = maxConnectionIdleTimeMS;
	}

	public long getMaintenanceInitialDelayMS() {
		return this.maintenanceInitialDelayMS;
	}

	public void setMaintenanceInitialDelayMS(long maintenanceInitialDelayMS) {
		this.maintenanceInitialDelayMS = maintenanceInitialDelayMS;
	}

	public long getMaintenanceFrequencyMS() {
		return this.maintenanceFrequencyMS;
	}

	public void setMaintenanceFrequencyMS(long maintenanceFrequencyMS) {
		this.maintenanceFrequencyMS = maintenanceFrequencyMS;
	}

	public int getMaxConnecting() {
		return this.maxConnecting;
	}

	public void setMaxConnecting(int maxConnecting) {
		this.maxConnecting = maxConnecting;
	}

	public String getReadConcernLevel() {
		return this.readConcernLevel;
	}

	public void setReadConcernLevel(String readConcernLevel) {
		this.readConcernLevel = readConcernLevel;
	}

	public String getWriteConcernW() {
		return this.writeConcernW;
	}

	public void setWriteConcernW(String writeConcernW) {
		this.writeConcernW = writeConcernW;
	}

	public long getWriteConcernWTimeout() {
		return this.writeConcernWTimeout;
	}

	public void setWriteConcernWTimeout(long writeConcernWTimeout) {
		this.writeConcernWTimeout = writeConcernWTimeout;
	}

	public boolean isWriteConcernJournal() {
		return this.writeConcernJournal;
	}

	public void setWriteConcernJournal(boolean writeConcernJournal) {
		this.writeConcernJournal = writeConcernJournal;
	}

	@ExcludeCodeCoverageGenerated
	@Override
	public String toString() {
		//@formatter:off
		return "MongoDBCluster{" +
				"authDB='" + this.authDB + '\'' +
				", hosts=" + this.hosts +
				", user='" + this.user + '\'' +
				", password='" + this.password + '\'' +
				", databaseName='" + this.databaseName + '\'' +
				", appName='" + this.appName + '\'' +
				", tagSet='" + this.tagSet + '\'' +
				", maxSize=" + this.maxSize +
				", minSize=" + this.minSize +
				", maxWaitTimeMS=" + this.maxWaitTimeMS +
				", maxConnectionLifeTimeMS=" + this.maxConnectionLifeTimeMS +
				", maxConnectionIdleTimeMS=" + this.maxConnectionIdleTimeMS +
				", maintenanceInitialDelayMS=" + this.maintenanceInitialDelayMS +
				", maintenanceFrequencyMS=" + this.maintenanceFrequencyMS +
				", maxConnecting=" + this.maxConnecting +
				", readConcernLevel='" + this.readConcernLevel + '\'' +
				", writeConcernW='" + this.writeConcernW + '\'' +
				", writeConcernWTimeout=" + this.writeConcernWTimeout +
				", writeConcernJournal=" + this.writeConcernJournal +
				'}';
		//@formatter:on
	}

}
