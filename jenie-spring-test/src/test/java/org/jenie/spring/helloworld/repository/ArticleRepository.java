package org.jenie.spring.helloworld.repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.bulk.BulkWriteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ArticleRepository extends MongoDBRepository {

	ArticleRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	private BulkWriteResult bulkWriteArticleHeader(String dbKey, List<ArticleHeader> articleHeaderList) {
		List<Document> articleHeaderWriteModelList = articleHeaderList.stream().map((articleHeader) -> {
			var doc = new Document();
			doc.put("_id", new ObjectId(articleHeader.id()));
			doc.put("boardId", articleHeader.board().id());
			doc.put("state", articleHeader.state());
			doc.put("title", articleHeader.title());
			doc.put("writer", articleHeader.writer());
			doc.put("actionDateTime.createdAt", Date.from(articleHeader.actionDateTime().getCreatedAt().toInstant()));
			return doc;

		}).collect(Collectors.toList());

		var template = this.mongoTemplateRouter.mongoTemplate(dbKey);
		var bulkWriteResult = template.bulkOps(BulkOperations.BulkMode.ORDERED, "article-header")
			.insert(articleHeaderWriteModelList)
			.execute();

		Assert.notNull(bulkWriteResult, "article-header bulkWriteResult must be not null");
		return bulkWriteResult;
	}

	public BulkWriteResult bulkWriteArticle(String dbKey, List<Article> articleList) {
		var articleHeaderList = articleList.stream().map(Article::header).collect(Collectors.toList());
		this.bulkWriteArticleHeader(dbKey, articleHeaderList);

		List<Document> articleContentList = articleList.stream().map((article) -> {
			var doc = new Document();
			doc.put("_id", new ObjectId(article.header().id()));
			doc.put("content", article.content());
			return doc;
		}).collect(Collectors.toList());

		var template = this.mongoTemplateRouter.mongoTemplate(dbKey);
		var bulkWriteArticleContent = template.bulkOps(BulkOperations.BulkMode.ORDERED, "article-content")
			.insert(articleContentList)
			.execute();

		Assert.notNull(bulkWriteArticleContent, "article-content bulkWriteResult must be not null");
		return bulkWriteArticleContent;
	}

	public List<String> getRandomIds(String dbKey, int size) {
		var aggregation = Aggregation.newAggregation(Aggregation.sample(size), Aggregation.project("_id"));
		AggregationResults<Document> results = this.mongoTemplateRouter.mongoTemplate(dbKey)
			.aggregate(aggregation, "article-header", Document.class);
		return results.getMappedResults()
			.stream()
			.map((doc) -> doc.getObjectId("_id").toString()) // ObjectId 변환
			.toList();
	}

}
