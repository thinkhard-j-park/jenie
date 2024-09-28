package org.jenie.spring.helloworld;

import java.time.ZonedDateTime;
import java.util.Comparator;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.pojo.Writer;
import org.jenie.spring.test.client.HttpClient;
import org.jenie.spring.test.helloworld.ArticleOperation;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArticleTests {

	private static final Logger logger = LoggerFactory.getLogger(ArticleTests.class);

	// TODO profile 별로 baseurl 설정 가능하게 할 것.
	protected final ArticleOperation articleOperation = new ArticleOperation(
			HttpClient.restClient("helloworld", "http://localhost:30000"));

	@Test
	void listArticleHeader() {
		// given
		var boardId = "";
		var param = new ListArticleHeaderRequestParam(boardId, "", 10, SortCode.TIME_DESC.getCode());

		// when
		var articleHeaderList = this.articleOperation.listArticleHeader("jenie-test", param);

		// then
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.list()).isNotEmpty();
		assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
			assertThat(articleHeader.board()).isNotNull();
			assertThat(articleHeader.board().id()).isNotEmpty();
			assertThat(articleHeader.title()).isNotEmpty();
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
		// given
		var writer = new Writer("uid", "name");
		var epochSecond = ZonedDateTime.now().toEpochSecond();
		var articleRequest = new ArticleRequest("test-board-id", "title-" + epochSecond, "content-" + epochSecond,
				writer);

		// when
		Article articleCreated = this.articleOperation.writeArticle("jenie-test", articleRequest);

		// then
		assertThat(articleCreated).isNotNull();
		assertThat(articleCreated.header()).isNotNull();
		assertThat(articleCreated.header().id()).isNotEmpty();
	}

	@Test
	void writeArticleWithInvalidBoard() {
		// given
		var writer = new Writer("uid", "name");
		var epochSecond = ZonedDateTime.now().toEpochSecond();
		var articleRequest = new ArticleRequest("unknown-board-id", "title-" + epochSecond, "content-" + epochSecond,
				writer);

		// when, then
		assertThatThrownBy(() -> this.articleOperation.writeArticle("jenie-test", articleRequest))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void modifyArticle() {

	}

	@Test
	void modifyArticleRollback() {

	}

}
