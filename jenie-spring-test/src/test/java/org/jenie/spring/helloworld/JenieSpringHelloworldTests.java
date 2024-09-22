package org.jenie.spring.helloworld;

import java.time.ZonedDateTime;
import java.util.Comparator;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.pojo.Writer;
import org.jenie.spring.test.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class JenieSpringHelloworldTests {

	private static final Logger logger = LoggerFactory.getLogger(JenieSpringHelloworldTests.class);

	protected final HelloworldOperation helloworldOperation = new HelloworldOperation(
			HttpClient.restClient("helloworld", "http://localhost:30000"));

	@Test
	void listArticleHeader() {
		var boardId = "";
		var param = new ListArticleHeaderRequestParam(boardId, "", 10, SortCode.TIME_DESC.getCode());
		var articleHeaderList = this.helloworldOperation.listArticleHeader("jenie-test", param);
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.list()).isNotEmpty();
		assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
			assertThat(articleHeader.writer()).isNotNull();
			assertThat(articleHeader.writer().getWid()).isNotEmpty();
			assertThat(articleHeader.actionDateTime()).isNotNull();
			assertThat(articleHeader.actionDateTime().getCreatedAt()).isNotNull();
			logger.info(articleHeader.toString());
		});
		assertThat(articleHeaderList.list()).isSortedAccordingTo(Comparator.comparing(ArticleHeader::id).reversed());
	}

	@Test
	void writeArticle() {
		var writer = new Writer("uid", "name");
		var epochSecond = ZonedDateTime.now().toEpochSecond();
		var articleRequest = new ArticleRequest("test-board-id", "title-" + epochSecond,
				"content-" + epochSecond, writer);
		Article articleCreated = this.helloworldOperation.writeArticle("jenie-test", articleRequest);
		assertThat(articleCreated).isNotNull();
		assertThat(articleCreated.header()).isNotNull();
		assertThat(articleCreated.header().id()).isNotEmpty();
	}

	@Test
	void writeArticleWithInvalidBoard() {
		// TODO 트랜잭션 실패시 제대로 동작하는지를 테스트 한다.
	}

}
