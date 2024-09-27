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

import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArticleTests {

	private static final Logger logger = LoggerFactory.getLogger(ArticleTests.class);

	protected final ArticleOperation articleOperation = new ArticleOperation(
			HttpClient.restClient("helloworld", "http://localhost:30000"));

	@Test
	void listArticleHeader() {
		var boardId = "";
		var param = new ListArticleHeaderRequestParam(boardId, "", 10, SortCode.TIME_DESC.getCode());
		var articleHeaderList = this.articleOperation.listArticleHeader("jenie-test", param);
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
		var writer = new Writer("uid", "name");
		var epochSecond = ZonedDateTime.now().toEpochSecond();
		var articleRequest = new ArticleRequest("test-board-id", "title-" + epochSecond, "content-" + epochSecond,
				writer);
		Article articleCreated = this.articleOperation.writeArticle("jenie-test", articleRequest);
		assertThat(articleCreated).isNotNull();
		assertThat(articleCreated.header()).isNotNull();
		assertThat(articleCreated.header().id()).isNotEmpty();
	}

	@Test
	void writeArticleWithInvalidBoard() {
		// TODO ProblemDetail을 넣어서 체크하게 하자.
		// TODO HttpStatus는 UnprocessableEntity 등으로 적절하게 체크하게 할 것.
		var writer = new Writer("uid", "name");
		var epochSecond = ZonedDateTime.now().toEpochSecond();
		var articleRequest = new ArticleRequest("unknown-board-id", "title-" + epochSecond, "content-" + epochSecond,
				writer);
		assertThatThrownBy(() -> this.articleOperation.writeArticle("jenie-test", articleRequest))
			.isInstanceOf(HttpServerErrorException.InternalServerError.class);
	}

}
