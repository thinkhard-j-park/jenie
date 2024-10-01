package org.jenie.spring.helloworld.test;

import java.util.Comparator;

import org.assertj.core.api.Assertions;
import org.jenie.spring.helloworld.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.pojo.Writer;
import org.jenie.spring.test.util.ZdtUtil;
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

	private Writer testWriter() {
		return new Writer("uid", "name");
	}

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
		Assertions.assertThat(articleHeaderList.list())
			.isSortedAccordingTo(Comparator.comparing(ArticleHeader::id).reversed());
	}

	private Article writeArticleAndVerify(String service, String boardId, String title, String content, Writer writer) {
		// given
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		// when
		Article articleCreated = this.articleOperation.writeArticle(service, articleRequest);

		// then
		assertThat(articleCreated).isNotNull();
		assertThat(articleCreated.header()).isNotNull();
		assertThat(articleCreated.header().id()).isNotEmpty();
		assertThat(articleCreated.header().board()).isNotNull();
		assertThat(articleCreated.header().board().id()).isEqualTo(boardId);
		assertThat(articleCreated.header().writer()).isNotNull();
		assertThat(articleCreated.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(articleCreated.header().title()).isEqualTo(title);
		assertThat(articleCreated.content()).isEqualTo(content);

		return articleCreated;
	}

	@Test
	void writeArticle() {
		var zdtNow = ZdtUtil.zdtNowString();
		this.writeArticleAndVerify("jenie-test", "test-board-id", "title-" + zdtNow, "content-" + zdtNow, testWriter());
	}

	@Test
	void writeArticleWithInvalidBoard() {
		var zdtNow = ZdtUtil.zdtNowString();
		assertThatThrownBy(() -> this.writeArticleAndVerify("jenie-test", "unknown-board-id", "title-" + zdtNow,
				"content-" + zdtNow, testWriter()))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void modifyArticle() {
		// given
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var createdArticle = this.writeArticleAndVerify(service, "test-board-id", "title-" + createdAt,
				"content-" + createdAt, writer);
		var articleHeader = createdArticle.header();

		var modifiedAt = ZdtUtil.zdtNowString();
		var boardId = articleHeader.board().id();
		var articleId = articleHeader.id();
		var title = "title-modified-" + modifiedAt;
		var content = "content-modified-" + modifiedAt;
		var modifyRequest = new ArticleRequest(boardId, title, content, writer);

		// when
		Article modifiedArticle = this.articleOperation.modifyArticle(service, articleId, modifyRequest);

		// then
		assertThat(modifiedArticle).isNotNull();
		assertThat(modifiedArticle.header()).isNotNull();
		assertThat(modifiedArticle.header().id()).isNotEmpty();
		assertThat(modifiedArticle.header().title()).isEqualTo(title);
		assertThat(modifiedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(modifiedArticle.content()).isEqualTo(content);
	}

	@Test
	void modifyArticleRollback() {

	}

}
