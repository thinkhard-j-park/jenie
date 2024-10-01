package org.jenie.spring.helloworld.operation;

import java.util.Map;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.test.client.Operation;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class ArticleOperation extends Operation {

	public ArticleOperation(RestClient restClient) {
		super(restClient);
	}

	public Article writeArticle(String service, ArticleRequest articleRequest) {
		return this.doPost("/{service}/article", Map.of("service", service), null, articleRequest,
				new ParameterizedTypeReference<>() {
				});
	}

	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		var queryParams = this.toQueryParam(param);
		return this.doGet("/{service}/article/list", Map.of("service", service), queryParams,
				new ParameterizedTypeReference<>() {
				});
	}

	public Article modifyArticle(String service, String articleId, ArticleRequest modifyRequest) {
		return this.doPut("/{service}/article/{id}", Map.of("service", service, "id", articleId), null, modifyRequest,
				new ParameterizedTypeReference<>() {
				});
	}

}
