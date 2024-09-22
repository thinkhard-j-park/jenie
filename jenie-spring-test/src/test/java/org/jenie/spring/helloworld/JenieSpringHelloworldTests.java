package org.jenie.spring.helloworld;

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
		var boardId = "test-board-id";
		var param = new ListArticleHeaderRequestParam(boardId, "", 10, SortCode.TIME_DESC.getCode());
		var articleHeaderList = this.helloOperation.listArticleHeader("jenie-test", param);
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.list()).isNotEmpty();
		assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
		});
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

	@Test
	void writeArticleWithInvalidBoard() {
		//TODO 트랜잭션 실패시 제대로 동작하는지를 테스트 한다.
	}

}
