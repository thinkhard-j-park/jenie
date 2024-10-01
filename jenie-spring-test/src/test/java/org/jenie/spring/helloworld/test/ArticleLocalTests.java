package org.jenie.spring.helloworld.test;

import java.time.ZonedDateTime;
import java.util.Comparator;

import org.assertj.core.api.Assertions;
import org.jenie.spring.helloworld.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.pojo.Writer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("local")
class ArticleLocalTests extends HelloworldTests {

	private static final Logger logger = LoggerFactory.getLogger(ArticleLocalTests.class);

	@Test
	void checkProperties() {
		assertThat(this.testProperties).isNotNull();
		assertThat(this.testProperties.getClientName()).isEqualTo("helloworld-local");
		assertThat(this.testProperties.getBaseUrl()).isEqualTo("http://localhost:30000");
	}

	@Test
	void listArticleHeader() {
		// given
		var boardId = "";
		var param = new ListArticleHeaderRequestParam(boardId, "", 10, SortCode.TIME_DESC.getCode());

		// when
		var articleHeaderList = this.articleOperation.listArticleHeader("jenie-test", param);

		// then
		Assertions.assertThat(articleHeaderList).isNotNull();
		Assertions.assertThat(articleHeaderList.list()).isNotEmpty();
		Assertions.assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			Assertions.assertThat(articleHeader.id()).isNotEmpty();
			Assertions.assertThat(articleHeader.board()).isNotNull();
			Assertions.assertThat(articleHeader.board().id()).isNotEmpty();
			Assertions.assertThat(articleHeader.title()).isNotEmpty();
			Assertions.assertThat(articleHeader.writer()).isNotNull();
			Assertions.assertThat(articleHeader.writer().getWid()).isNotEmpty();
			Assertions.assertThat(articleHeader.actionDateTime()).isNotNull();
			Assertions.assertThat(articleHeader.actionDateTime().getCreatedAt()).isNotNull();
			logger.info(articleHeader.toString());
		});
		Assertions.assertThat(articleHeaderList.list()).isSortedAccordingTo(Comparator.comparing(ArticleHeader::id).reversed());
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
