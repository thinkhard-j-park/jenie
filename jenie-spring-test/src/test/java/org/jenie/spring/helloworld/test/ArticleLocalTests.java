package org.jenie.spring.helloworld.test;

import java.util.Comparator;

import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.util.ZdtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("local")
class ArticleLocalTests extends HelloworldTests {

	private static final Logger logger = LoggerFactory.getLogger(ArticleLocalTests.class);

	private TestInfo testInfo;

	private Writer testWriter() {
		return new Writer("uid", "name");
	}

	@BeforeEach
	void init(TestInfo testInfo) {
		this.testInfo = testInfo;
	}

	private Article writeArticleAndVerify(String service, String boardId, String title, String content, Writer writer) {
		// given
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		// when
		Article createdArticle = this.articleOperation.writeArticle(service, articleRequest);

		// then
		assertThat(createdArticle).isNotNull();
		assertThat(createdArticle.header()).isNotNull();
		assertThat(createdArticle.header().id()).isNotEmpty();
		assertThat(createdArticle.header().board()).isNotNull();
		assertThat(createdArticle.header().state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(createdArticle.header().board().id()).isEqualTo(boardId);
		assertThat(createdArticle.header().writer()).isNotNull();
		assertThat(createdArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(createdArticle.header().title()).isEqualTo(title);
		assertThat(createdArticle.content()).isEqualTo(content);

		return createdArticle;
	}

	@Test
	void checkProperties() {
		assertThat(this.testProperties).isNotNull();
		assertThat(this.testProperties.getClientName()).isEqualTo("helloworld-local");
		assertThat(this.testProperties.getBaseUrl()).isEqualTo("http://localhost:30000");
	}

	@Test
	void getArticleHeaderById() {
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		var article = this.writeArticleAndVerify(service, boardId, title, "content-" + zdtNow, writer);
		assertThat(article).isNotNull();
		assertThat(article.header()).isNotNull();
		assertThat(article.header().id()).isNotEmpty();

		var articleId = article.header().id();
		var fetchedArticleHeader = this.articleOperation.getArticleByHeader(service, articleId, true);
		assertThat(fetchedArticleHeader).isNotNull();
		assertThat(fetchedArticleHeader.id()).isEqualTo(articleId);
		assertThat(fetchedArticleHeader.board().id()).isEqualTo(boardId);
		assertThat(fetchedArticleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(fetchedArticleHeader.title()).isEqualTo(title);
		assertThat(fetchedArticleHeader.writer()).isNotNull();
		assertThat(fetchedArticleHeader.writer().getWid()).isEqualTo(writer.getWid());
	}

	@Test
	void viewArticle() {
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		var article = this.writeArticleAndVerify(service, boardId, title, "content-" + zdtNow, writer);
		assertThat(article).isNotNull();
		assertThat(article.header()).isNotNull();
		assertThat(article.header().id()).isNotEmpty();

		var articleId = article.header().id();
		var fetchedArticle = this.articleOperation.viewArticle(service, articleId, true);
		assertThat(fetchedArticle).isNotNull();
		assertThat(fetchedArticle.header()).isNotNull();
		assertThat(fetchedArticle.header().id()).isEqualTo(articleId);
		assertThat(fetchedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(fetchedArticle.header().state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(fetchedArticle.header().title()).isEqualTo(title);
		assertThat(fetchedArticle.header().writer()).isNotNull();
		assertThat(fetchedArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(fetchedArticle.content()).isEqualTo(article.content());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "test-board-id" })
	void listArticleHeader(String targetBoardId) {
		// 목록 보기할 데이터가 없을 수 있으므로 테스트를 위한 데이터를 생성한다.
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		for (int i = 0; i < 10; i++) {
			writeArticleAndVerify(service, boardId, title, "content-" + zdtNow, writer);
		}

		// 목록보기
		// given
		var listArticleHeaderRequestParam = new ListArticleHeaderRequestParam(targetBoardId, "", 5,
				SortCode.TIME_DESC.getCode());

		// when
		var articleHeaderList = this.articleOperation.listArticleHeader(service, listArticleHeaderRequestParam);

		// then
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.list()).isNotEmpty();
		assertThat(articleHeaderList.hasMore()).isTrue();
		assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
			assertThat(articleHeader.board()).isNotNull();
			assertThat(articleHeader.board().id()).isNotEmpty();
			assertThat(articleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
			assertThat(articleHeader.title()).isNotEmpty();
			assertThat(articleHeader.writer()).isNotNull();
			assertThat(articleHeader.writer().getWid()).isNotEmpty();
			assertThat(articleHeader.actionDateTime()).isNotNull();
			assertThat(articleHeader.actionDateTime().getCreatedAt()).isNotNull();
			logger.info(articleHeader.toString());
		});
		assertThat(articleHeaderList.list()).isSortedAccordingTo(Comparator.comparing(ArticleHeader::id).reversed());

		// 목록보기 더보기
		// given
		var previousArticleHeader = articleHeaderList.list().getLast();
		var listMoreArticleHeaderRequestParam = new ListArticleHeaderRequestParam(targetBoardId,
				previousArticleHeader.id(), 5, SortCode.TIME_DESC.getCode());

		// when
		var moreArticleHeaderList = this.articleOperation.listArticleHeader("jenie-test",
				listMoreArticleHeaderRequestParam);

		// then
		assertThat(moreArticleHeaderList).isNotNull();
		assertThat(moreArticleHeaderList.list()).isNotEmpty();
		assertThat(moreArticleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
			assertThat(articleHeader.id()).isLessThan(previousArticleHeader.id());
			assertThat(articleHeader.board()).isNotNull();
			assertThat(articleHeader.board().id()).isNotEmpty();
			assertThat(articleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
			assertThat(articleHeader.title()).isNotEmpty();
			assertThat(articleHeader.writer()).isNotNull();
			assertThat(articleHeader.writer().getWid()).isNotEmpty();
			assertThat(articleHeader.actionDateTime()).isNotNull();
			assertThat(articleHeader.actionDateTime().getCreatedAt()).isNotNull();
			assertThat(articleHeader.actionDateTime().getCreatedAt())
				.isBefore(previousArticleHeader.actionDateTime().getCreatedAt());
			logger.info(articleHeader.toString());
		});
		assertThat(moreArticleHeaderList.list())
			.isSortedAccordingTo(Comparator.comparing(ArticleHeader::id).reversed());
	}

	@Test
	void writeArticle() {
		var zdtNow = ZdtUtil.zdtNowString();
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var content = "content-" + title;

		this.writeArticleAndVerify("jenie-test", "test-board-id", title, content, testWriter());
	}

	@Test
	void writeArticleWithInvalidBoard() {
		var zdtNow = ZdtUtil.zdtNowString();
		assertThatThrownBy(() -> this.writeArticleAndVerify("jenie-test", "unknown-board-id",
				this.testInfo.getDisplayName() + "-" + zdtNow, "content-" + zdtNow, testWriter()))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void modifyArticle() {
		// given
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var content = "content-" + title;
		var createdArticle = this.writeArticleAndVerify(service, "test-board-id", title, content, writer);
		var articleHeader = createdArticle.header();

		var modifiedAt = ZdtUtil.zdtNowString();
		var boardId = articleHeader.board().id();
		var articleId = articleHeader.id();
		var titleToModify = "modified-" + this.testInfo.getDisplayName() + "-" + modifiedAt;
		var contentToModify = "modified-content-" + this.testInfo.getDisplayName() + "-" + modifiedAt;
		var modifyRequest = new ArticleRequest(boardId, titleToModify, contentToModify, writer);

		// when
		Article modifiedArticle = this.articleOperation.modifyArticle(service, articleId, modifyRequest);

		// then
		assertThat(modifiedArticle).isNotNull();
		assertThat(modifiedArticle.header()).isNotNull();
		assertThat(modifiedArticle.header().id()).isNotEmpty();
		assertThat(modifiedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(modifiedArticle.header().state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(modifiedArticle.header().title()).isEqualTo(titleToModify);
		assertThat(modifiedArticle.content()).isEqualTo(contentToModify);
	}

	@Test
	void modifyArticleRollback() {
		// given
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var createdArticle = this.writeArticleAndVerify(service, "test-board-id", title, "content-" + createdAt,
				writer);
		var articleHeader = createdArticle.header();

		var modifiedAt = ZdtUtil.zdtNowString();
		var boardId = articleHeader.board().id();
		var articleId = articleHeader.id();
		var titleToModify = this.testInfo.getDisplayName() + "-toModify-" + modifiedAt;
		var content = ""; // content 가 empty 인 경우는 게시글이 수정되지 않아야 한다.
		var modifyRequest = new ArticleRequest(boardId, titleToModify, content, writer);

		// when
		assertThatThrownBy(() -> this.articleOperation.modifyArticle(service, articleId, modifyRequest))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);

		// then
		var fetchedArticleHeader = this.articleOperation.getArticleByHeader(service, articleId, true);
		assertThat(fetchedArticleHeader).isNotNull();
		assertThat(fetchedArticleHeader.id()).isEqualTo(articleId);
		assertThat(fetchedArticleHeader.board().id()).isEqualTo(boardId);
		assertThat(fetchedArticleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(fetchedArticleHeader.title()).isEqualTo(title);
		assertThat(fetchedArticleHeader.actionDateTime().getUpdatedAt()).isNull();
	}

	@Test
	void deleteArticle() {
		// given
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var content = "content-" + title;
		var createdArticle = this.writeArticleAndVerify(service, "test-board-id", title, content, writer);
		var articleHeader = createdArticle.header();

		// when
		var articleDeleteResult = this.articleOperation.deleteArticle(service, articleHeader.id());

		// then
		assertThat(articleDeleteResult).isNotNull();
		assertThat(articleDeleteResult.id()).isEqualTo(articleHeader.id());
		assertThat(articleDeleteResult.state()).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(articleDeleteResult.deletedAt()).isNotNull();
	}

	@Test
	void deleteArticleWithInvalidId() {
		// given
		var service = "jenie-test";
		var articleId = "unknown-id";

		// when, then
		assertThatThrownBy(() -> this.articleOperation.deleteArticle(service, articleId))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

}
