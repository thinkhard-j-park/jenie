package org.jenie.spring.helloworld.test;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.jenie.spring.client.Constant.Protocol;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.operation.ArticleOperation;
import org.jenie.spring.helloworld.utils.ZdtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ProblemDetail;
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

	@Test
	void checkProperties() {
		assertThat(this.testProperties).isNotNull();
		assertThat(this.testProperties.getClientName()).isEqualTo("helloworld-local");
		assertThat(this.testProperties.getBaseUrl()).isEqualTo("http://localhost:30000");
	}

	@Test
	void checkRestOperation() {
		assertThat(this.articleRestOperation).isNotNull();
	}

	@Test
	void checkGrpcOperation() {
		assertThat(this.articleGrpcOperation).isNotNull();
	}

	static Stream<Arguments> provideProtocol() {
		return Stream.of(Arguments.of(Protocol.rest), Arguments.of(Protocol.grpc));
	}

	private Article writeArticleAndVerify(ArticleOperation articleOperation, String service,
			ArticleRequest articleRequest) {
		// given, when
		Article createdArticle = articleOperation.writeArticle(service, articleRequest);

		// then
		assertThat(createdArticle).isNotNull();
		assertThat(createdArticle.header()).isNotNull();
		assertThat(createdArticle.header().id()).isNotEmpty();
		assertThat(createdArticle.header().board()).isNotNull();
		assertThat(createdArticle.header().state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(createdArticle.header().board().id()).isEqualTo(articleRequest.boardId());
		assertThat(createdArticle.header().writer()).isNotNull();
		assertThat(createdArticle.header().writer().getWid()).isEqualTo(articleRequest.writer().getWid());
		assertThat(createdArticle.header().title()).isEqualTo(articleRequest.title());
		assertThat(createdArticle.content()).isEqualTo(articleRequest.content());

		return createdArticle;
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void getArticleHeaderById(Protocol protocol) {
		var articleOperation = articleOperation(protocol);
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		var articleRequest = new ArticleRequest(boardId, title, "content-" + zdtNow, writer);
		var article = this.writeArticleAndVerify(articleOperation, service, articleRequest);
		assertThat(article).isNotNull();
		assertThat(article.header()).isNotNull();
		assertThat(article.header().id()).isNotEmpty();

		var articleId = article.header().id();
		var fetchedArticleHeader = articleOperation.getArticleByHeader(service, articleId, true);
		assertThat(fetchedArticleHeader).isNotNull();
		assertThat(fetchedArticleHeader.id()).isEqualTo(articleId);
		assertThat(fetchedArticleHeader.board().id()).isEqualTo(boardId);
		assertThat(fetchedArticleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(fetchedArticleHeader.title()).isEqualTo(title);
		assertThat(fetchedArticleHeader.writer()).isNotNull();
		assertThat(fetchedArticleHeader.writer().getWid()).isEqualTo(writer.getWid());
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void viewArticle(Protocol protocol) {
		var articleOperation = articleOperation(protocol);
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		var articleRequest = new ArticleRequest(boardId, title, "content-" + zdtNow, writer);
		var article = this.writeArticleAndVerify(articleOperation, service, articleRequest);
		assertThat(article).isNotNull();
		assertThat(article.header()).isNotNull();
		assertThat(article.header().id()).isNotEmpty();

		var articleId = article.header().id();
		var fetchedArticle = articleOperation.viewArticle(service, articleId, true);
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

	static Stream<Arguments> provideProtocolBoardId() {
		return Stream.of(Arguments.of(Protocol.rest, ""), Arguments.of(Protocol.rest, "test-board-id"),
				Arguments.of(Protocol.grpc, ""), Arguments.of(Protocol.grpc, "test-board-id"));
	}

	@ParameterizedTest
	@MethodSource("provideProtocolBoardId")
	void listArticleHeader(Protocol protocol, String targetBoardId) {
		var articleOperation = articleOperation(protocol);

		// 목록 보기할 데이터가 없을 수 있으므로 테스트를 위한 데이터를 생성한다.
		var zdtNow = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var boardId = "test-board-id";
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var writer = testWriter();
		var articleRequest = new ArticleRequest(boardId, title, "content-" + zdtNow, writer);
		for (int i = 0; i < 10; i++) {
			writeArticleAndVerify(articleOperation, service, articleRequest);
		}

		// 목록보기
		// given
		var listArticleHeaderRequestParam = new ListArticleHeaderRequestParam(targetBoardId, "", 5,
				SortCode.TIME_DESC.getCode());

		// when
		var articleHeaderList = articleOperation.listArticleHeader(service, listArticleHeaderRequestParam);

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
		var moreArticleHeaderList = articleOperation.listArticleHeader(service, listMoreArticleHeaderRequestParam);

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

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void writeArticle(Protocol protocol) {
		var articleOperation = articleOperation(protocol);
		var zdtNow = ZdtUtil.zdtNowString();
		var title = this.testInfo.getDisplayName() + "-" + zdtNow;
		var content = "content-" + title;
		var articleRequest = new ArticleRequest("test-board-id", title, content, testWriter());
		this.writeArticleAndVerify(articleOperation, "jenie-test", articleRequest);
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void writeArticleWithInvalidBoard(Protocol protocol) {
		var articleOperation = articleOperation(protocol);
		var zdtNow = ZdtUtil.zdtNowString();
		var articleRequest = new ArticleRequest("unknown-board-id", this.testInfo.getDisplayName() + "-" + zdtNow,
				"content-" + zdtNow, testWriter());
		assertThatThrownBy(() -> this.writeArticleAndVerify(articleOperation, "jenie-test", articleRequest))
			.isInstanceOfAny(HttpClientErrorException.BadRequest.class, StatusRuntimeException.class)
			.satisfies((throwable) -> verifyErrorResponse(throwable, ErrorCode.BOARD_NOT_FOUND));
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void modifyArticle(Protocol protocol) {
		// given
		var articleOperation = articleOperation(protocol);
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var content = "content-" + title;
		var articleRequest = new ArticleRequest("test-board-id", title, content, writer);
		var createdArticle = this.writeArticleAndVerify(articleOperation, service, articleRequest);
		var articleHeader = createdArticle.header();

		var modifiedAt = ZdtUtil.zdtNowString();
		var boardId = articleHeader.board().id();
		var articleId = articleHeader.id();
		var titleToModify = "modified-" + this.testInfo.getDisplayName() + "-" + modifiedAt;
		var contentToModify = "modified-content-" + this.testInfo.getDisplayName() + "-" + modifiedAt;
		var modifyRequest = new ArticleRequest(boardId, titleToModify, contentToModify, writer);

		// when
		Article modifiedArticle = articleOperation.modifyArticle(service, articleId, modifyRequest);

		// then
		assertThat(modifiedArticle).isNotNull();
		assertThat(modifiedArticle.header()).isNotNull();
		assertThat(modifiedArticle.header().id()).isNotEmpty();
		assertThat(modifiedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(modifiedArticle.header().state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(modifiedArticle.header().title()).isEqualTo(titleToModify);
		assertThat(modifiedArticle.content()).isEqualTo(contentToModify);
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void modifyArticleRollback(Protocol protocol) {
		// given
		var articleOperation = articleOperation(protocol);
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var articleRequest = new ArticleRequest("test-board-id", title, "content-" + createdAt, writer);
		var createdArticle = this.writeArticleAndVerify(articleOperation, service, articleRequest);
		var articleHeader = createdArticle.header();

		var modifiedAt = ZdtUtil.zdtNowString();
		var boardId = articleHeader.board().id();
		var articleId = articleHeader.id();
		var titleToModify = this.testInfo.getDisplayName() + "-toModify-" + modifiedAt;
		var content = ""; // content 가 empty 인 경우는 게시글이 수정되지 않아야 한다.
		var modifyRequest = new ArticleRequest(boardId, titleToModify, content, writer);

		// when
		assertThatThrownBy(() -> articleOperation.modifyArticle(service, articleId, modifyRequest))
			.isInstanceOfAny(HttpClientErrorException.BadRequest.class, StatusRuntimeException.class)
			.satisfies((throwable) -> verifyErrorResponse(throwable, ErrorCode.ILLEGAL_DATA));

		// then
		var fetchedArticleHeader = articleOperation.getArticleByHeader(service, articleId, true);
		assertThat(fetchedArticleHeader).isNotNull();
		assertThat(fetchedArticleHeader.id()).isEqualTo(articleId);
		assertThat(fetchedArticleHeader.board().id()).isEqualTo(boardId);
		assertThat(fetchedArticleHeader.state()).isEqualTo(ArticleState.Normal.getCode());
		assertThat(fetchedArticleHeader.title()).isEqualTo(title);
		assertThat(fetchedArticleHeader.actionDateTime().getUpdatedAt()).isNull();
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void deleteArticle(Protocol protocol) {
		// given
		var articleOperation = articleOperation(protocol);
		var createdAt = ZdtUtil.zdtNowString();
		var service = "jenie-test";
		var writer = testWriter();
		var title = this.testInfo.getDisplayName() + "-" + createdAt;
		var content = "content-" + title;
		var articleRequest = new ArticleRequest("test-board-id", title, content, writer);
		var createdArticle = this.writeArticleAndVerify(articleOperation, service, articleRequest);
		var articleHeader = createdArticle.header();

		// when
		var articleDeleteResult = articleOperation.deleteArticle(service, articleHeader.id());

		// then
		assertThat(articleDeleteResult).isNotNull();
		assertThat(articleDeleteResult.id()).isEqualTo(articleHeader.id());
		assertThat(articleDeleteResult.state()).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(articleDeleteResult.deletedAt()).isNotNull();
	}

	@ParameterizedTest
	@MethodSource("provideProtocol")
	void deleteArticleWithInvalidId(Protocol protocol) {
		// given
		var articleOperation = articleOperation(protocol);
		var service = "jenie-test";
		var articleId = "unknown-id";

		// when, then
		assertThatThrownBy(() -> articleOperation.deleteArticle(service, articleId))
			.isInstanceOfAny(HttpClientErrorException.BadRequest.class, StatusRuntimeException.class)
			.satisfies((throwable) -> verifyErrorResponse(throwable, ErrorCode.ILLEGAL_DATA));
	}

	void verifyErrorResponse(Throwable throwable, ErrorCode errorCode) {
		if (throwable instanceof HttpClientErrorException.BadRequest badRequestException) {
			assertThat(badRequestException).isNotNull();

			var problemDetail = badRequestException.getResponseBodyAs(ProblemDetail.class);
			assertThat(problemDetail).isNotNull();
			assertThat(problemDetail.getTitle()).isEqualTo(errorCode.getTitle());
			assertThat(Objects.requireNonNull(problemDetail.getProperties()).get("error-code"))
				.isEqualTo(String.valueOf(errorCode.getCode()));
		}

		if (throwable instanceof StatusRuntimeException statusRuntimeException) {
			Status status = statusRuntimeException.getStatus();
			assertThat(status).isNotNull();
			assertThat(status.getCode()).isEqualTo(errorCode.getGrpcStatus());

			Metadata metadata = statusRuntimeException.getTrailers();
			assertThat(metadata).isNotNull();
			assertThat(metadata.get(Metadata.Key.of("title", Metadata.ASCII_STRING_MARSHALLER)))
				.isEqualTo(errorCode.getTitle());
			assertThat(metadata.get(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER)))
				.isEqualTo(String.valueOf(errorCode.getCode()));
		}

	}

}
