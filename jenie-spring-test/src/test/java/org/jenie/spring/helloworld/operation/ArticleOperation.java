package org.jenie.spring.helloworld.operation;

import java.util.Map;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.test.client.Operation;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.LinkedMultiValueMap;
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

	public Article viewArticle(String service, String articleId, boolean incViewCount) {
		var queryParams = new LinkedMultiValueMap<String, String>();
		queryParams.add("incViewCount", Boolean.toString(incViewCount));
		return this.doGet("/{service}/article/{id}", Map.of("service", service, "id", articleId), queryParams,
				new ParameterizedTypeReference<>() {
				});
	}

	public ArticleHeader getArticleByHeader(String service, String articleId, boolean latest) {
		var queryParams = new LinkedMultiValueMap<String, String>();
		queryParams.add("latest", Boolean.toString(latest));
		return this.doGet("/{service}/article/{id}/header", Map.of("service", service, "id", articleId), queryParams,
				new ParameterizedTypeReference<>() {
				});
	}

	public ArticleDeleteResult deleteArticle(String service, String articleId) {
		return this.doDelete("/{service}/article/{id}", Map.of("service", service, "id", articleId), null,
				new ParameterizedTypeReference<>() {
				});
	}

}
