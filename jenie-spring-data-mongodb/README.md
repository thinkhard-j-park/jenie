# Jenie Spring Data MongoDB
This module provides access to multiple MongoDB clusters and databases based on keys.

## Setting up multiple mongodb cluster access
### Routing configuration
- Each mongo cluster should create a 'dbconn' database with the schema below. The database name can be set differently via options.
- Configure the 'dbconn' collection within this database.

```mongodb-json
{
  "_id" : "...",
  "clusterKey" : "dckr",
  "dbKey" : "seoul",
  "dbName" : "seoul"
},
{
  "_id" : "...",
  "clusterKey" : "dckr",
  "dbKey" : "yongin",
  "dbName" : "yongin"
},
{
  "_id" : "...",
  "clusterKey" : "dcus",
  "dbKey" : "newyork",
  "dbName" : "newyork"
}
```
- The 'dbconn' collection contains database routing information.
- The settings above allow access to 2 mongo clusters: dckr, dcus and 3 databases: seoul, yongin, newyork.
- The name of the database containing the 'dbconn' collection can be changed through the following setting:

| Property                                            | Purpose                                              | Description         |
|-----------------------------------------------------|------------------------------------------------------|---------------------|
| mongodb.setting.cluster.${clusterKey}.database-name |Database containing connection information. | Default is 'dbconn' |
- The 'dbconn' collection is accessed by the application to read and cache the configured dbKey and dbName.
- Queries are executed on the database specified by dbKey using [MongoTemplateRouter](src/main/java/org/jenie/spring/data/mongodb/operation/MongoTemplateRouter.java).


### Application Settings
The application accesses the 'dbconn' database of the cluster configured with the options below to read database routing information

| Property                                    | Purpose              | Description                                               |
|---------------------------------------------|----------------------|-----------------------------------------------------------|
| mongodb.setting.enabled                     | Module usage setting             | Default is false                                          |
| mongodb.setting.app-name                    | Application name connecting to MongoDB | -                                                         |
| mongodb.setting.cluster.${clusterKey}.hosts | MongoDB connection address           | e.g. mongodb.setting.cluster.dckr.host[0]=localhost:27017 | 

- For detailed options, refer to [MongoDBSetting](src/main/java/org/jenie/spring/data/mongodb/connector/MongoDBSetting.java).

## Usage Example

### MongoTemplateRouter
- Retrieves a template to access the database based on keys.
```java

protected final MongoTemplateRouter mongoTemplateRouter;

public Writer findWriterById(String dbKey, String id) {
	var query = Query.query(Criteria.where("_id").is(id));
	return this.mongoTemplateRouter.mongoTemplate(dbKey).findOne(query, Writer.class);
}


public ArticleHeaderEntity modifyArticleHeader(String dbKey, String id, String title) {
	var update = new Update();
	update.set("title", title);
	return this.mongoTemplateRouter.mongoTemplate(dbKey, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(id)), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
}
```

### Transaction

MongoDB transactions work only on replica sets or sharded clusters. For more details, refer to [MongoDB Official Document](https://www.mongodb.com/docs/manual/core/transactions). 
- Provides the MongoKeyBasedTransactional annotation, an extension of Spring's Transactional.
```java

@MongoKeyBasedTransactional
public void txMethod(@DBKey String dbKey, String data) {
    // some logic
}

@MongoKeyBasedTransactional(key = "dbKey")
public void txMethod(String data) {
    // some logic
}

record TestDto(String service, String name) {
}

@MongoKeyBasedTransactional(expr = "#dto.service")
String txMethod(TestDto dto, String someValue) {
    // some logic
}
```
- For more detailed usage, refer to  [MongoKeyBasedTransactionAspectTests](src/test/java/org/jenie/spring/data/mongodb/transaction/MongoKeyBasedTransactionAspectTests.java).
