package org.jenie.spring.test.helloworld;

import org.jenie.spring.helloworld.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.pojo.Writer;
import org.jenie.spring.test.client.HttpClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JenieSpringHelloworldTests {

	protected final HelloworldOperation helloOperation = new HelloworldOperation(
			HttpClient.restClient("helloworld", "http://localhost:30000"));

	@Test
	void listArticleHeader() {
		var param = new ListArticleHeaderRequestParam("test-board-id", "", 10, SortCode.TIME_DESC.getCode());
		var articleHeaderList = this.helloOperation.listArticleHeader("jenie-test", param);
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.list()).isNotEmpty();
	}

	@Test
	void writeArticle() {
		var writer = new Writer("uid", "name");
		var articleRequest = new ArticleRequest("test-board-id", "title", "content", writer);
		Article articleCreated = this.helloOperation.writeArticle("jenie-test", articleRequest);
		assertThat(articleCreated).isNotNull();
		assertThat(articleCreated.header()).isNotNull();
		assertThat(articleCreated.header().id()).isNotEmpty();
	}

}
