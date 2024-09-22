package org.jenie.spring.helloworld.repository;

import org.jenie.spring.test.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleContentRepository extends MongoDBRepository {

	public ArticleContentRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	public ArticleContentEntity insert(String dbKey, ArticleContentEntity content) {
		return this.insert(this.mongoTemplateRouter.mongoTemplate(dbKey), content);
	}

	public ArticleContentEntity insert(MongoTemplate template, ArticleContentEntity content) {
		return template.insert(content);
	}

}
