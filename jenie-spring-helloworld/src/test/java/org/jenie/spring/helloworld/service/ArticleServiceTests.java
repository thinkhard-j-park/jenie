package org.jenie.spring.helloworld.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.ArticleErrors;
import org.jenie.spring.helloworld.exception.BoardErrors;
import org.jenie.spring.helloworld.repository.ArticleContentRepository;
import org.jenie.spring.helloworld.repository.ArticleHeaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTests {

	@InjectMocks
	private ArticleService articleService;

	@Mock
	private BoardService boardService;

	@Mock
	private ArticleHeaderRepository articleHeaderRepository;

	@Mock
	private ArticleContentRepository articleContentRepository;

	@Test
	void getArticleHeaderById() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setBoardId(boardId);

		given(this.articleHeaderRepository.findArticleHeaderById(service, id, false)).willReturn(headerEntity);

		// when
		var result = this.articleService.getArticleHeaderById(service, id, false);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(id);
		assertThat(result.board()).isNotNull();
		assertThat(result.board().id()).isEqualTo(boardId);
	}

	@Test
	void listArticleHeader() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var writer = new Writer("wid", "name");
		var title = "title";
		var param = new ListArticleHeaderRequestParam(boardId, "", 5, SortCode.TIME_DESC.getCode());
		var entityList = new ArrayList<ArticleHeaderEntity>();
		for (int i = 0; i < 6; i++) {
			var headerEntity = new ArticleHeaderEntity();
			headerEntity.setId("article-id-" + i);
			headerEntity.setBoardId(boardId);
			headerEntity.setTitle(title);
			headerEntity.setWriter(writer);
			entityList.add(headerEntity);
		}

		given(this.articleHeaderRepository.listArticleHeader(service, param)).willReturn(entityList);
		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);

		// when
		var articleHeaderList = this.articleService.listArticleHeader(service, param);

		// then
		assertThat(articleHeaderList).isNotNull();
		assertThat(articleHeaderList.hasMore()).isTrue();
		assertThat(articleHeaderList.list()).hasSize(5);
		assertThat(articleHeaderList.list()).allSatisfy((articleHeader) -> {
			assertThat(articleHeader.id()).isNotEmpty();
			assertThat(articleHeader.board()).isNotNull();
			assertThat(articleHeader.board().id()).isEqualTo(boardId);
			assertThat(articleHeader.title()).isEqualTo(title);
			assertThat(articleHeader.writer()).isNotNull();
			assertThat(articleHeader.writer().getWid()).isEqualTo(writer.getWid());
		});
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void viewArticle(boolean incViewCount) {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var id = "article-id";
		var title = "title";
		var content = "content";
		var writer = new Writer("wid", "name");

		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setBoardId(boardId);
		headerEntity.setTitle(title);
		headerEntity.setWriter(writer);
		var contentEntity = new ArticleContentEntity();
		contentEntity.setId(id);
		contentEntity.setContent(content);

		given(this.articleHeaderRepository.findArticleHeaderById(service, id, false)).willReturn(headerEntity);
		given(this.articleContentRepository.findArticleContentById(service, id)).willReturn(contentEntity);
		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);

		// when
		var fetchedArticle = this.articleService.viewArticle(service, id, incViewCount);

		// then
		assertThat(fetchedArticle).isNotNull();
		assertThat(fetchedArticle.header()).isNotNull();
		assertThat(fetchedArticle.header().id()).isEqualTo(id);
		assertThat(fetchedArticle.header().board()).isNotNull();
		assertThat(fetchedArticle.header().board().id()).isNotNull();
		assertThat(fetchedArticle.content()).isEqualTo(content);
	}

	@Test
	void writeArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var id = "article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setTitle(title);
		articleHeaderEntity.setBoardId(boardId);
		articleHeaderEntity.setWriter(writer);

		var articleContentEntity = new ArticleContentEntity();
		articleContentEntity.setId(id);
		articleContentEntity.setContent(content);

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);
		given(this.articleHeaderRepository.insert(eq(service), any(ArticleHeaderEntity.class)))
			.willReturn(articleHeaderEntity);
		given(this.articleContentRepository.insert(eq(service), any(ArticleContentEntity.class)))
			.willReturn(articleContentEntity);

		// when
		var createdArticle = this.articleService.writeArticle(service, articleRequest);

		// then
		assertThat(createdArticle).isNotNull();
		assertThat(createdArticle.header()).isNotNull();
		assertThat(createdArticle.header().id()).isEqualTo(id);
		assertThat(createdArticle.header().board()).isNotNull();
		assertThat(createdArticle.header().board().id()).isEqualTo(boardId);
		assertThat(createdArticle.header().title()).isEqualTo(title);
		assertThat(createdArticle.header().writer()).isNotNull();
		assertThat(createdArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(createdArticle.content()).isEqualTo(content);
	}

	@Test
	void writerArticleWithInvalidBoardShouldFail() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(null);

		// when, then
		assertThatThrownBy(() -> this.articleService.writeArticle(service, articleRequest))
			.isInstanceOf(BoardErrors.BoardNotFoundException.class);
	}

	@Test
	void modifyArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var id = "article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleModifyRequest = new ArticleRequest(boardId, title, content, writer);

		var articleHeaderEntity = new ArticleHeaderEntity();
		articleHeaderEntity.setId(id);
		articleHeaderEntity.setTitle(title);
		articleHeaderEntity.setBoardId(boardId);
		articleHeaderEntity.setWriter(writer);

		var articleContentEntity = new ArticleContentEntity();
		articleContentEntity.setId(id);
		articleContentEntity.setContent(content);

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);
		given(this.articleHeaderRepository.findArticleWriterById(service, id)).willReturn(writer);
		given(this.articleHeaderRepository.modifyArticleHeader(service, id, title)).willReturn(articleHeaderEntity);
		given(this.articleContentRepository.modifyArticleContent(service, id, content))
			.willReturn(articleContentEntity);

		// when
		var modifiedArticle = this.articleService.modifyArticle(service, id, articleModifyRequest);

		// then
		assertThat(modifiedArticle).isNotNull();
		assertThat(modifiedArticle.header()).isNotNull();
		assertThat(modifiedArticle.header().id()).isEqualTo(id);
		assertThat(modifiedArticle.header().board()).isNotNull();
		assertThat(modifiedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(modifiedArticle.header().title()).isEqualTo(title);
		assertThat(modifiedArticle.header().writer()).isNotNull();
		assertThat(modifiedArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(modifiedArticle.content()).isEqualTo(content);
	}

	@Test
	void modifyArticleWithInvalidBoardShouldFail() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "invalid-article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleModifyRequest = new ArticleRequest(boardId, title, content, writer);

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(null);

		// when, then
		assertThatThrownBy(() -> this.articleService.modifyArticle(service, id, articleModifyRequest))
			.isInstanceOf(BoardErrors.BoardNotFoundException.class);
	}

	@Test
	void modifyArticleWithInvalidIdShouldFail() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var id = "invalid-article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleModifyRequest = new ArticleRequest(boardId, title, content, writer);

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);
		given(this.articleHeaderRepository.findArticleWriterById(service, id)).willReturn(null);

		// when, then
		assertThatThrownBy(() -> this.articleService.modifyArticle(service, id, articleModifyRequest))
			.isInstanceOf(ArticleErrors.ArticleNotFoundException.class);
	}

	@Test
	void modifyArticleWithInvalidWriterIdShouldFail() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var boardEntity = new BoardEntity();
		boardEntity.setId(boardId);
		var id = "invalid-article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleModifyRequest = new ArticleRequest(boardId, title, content, writer);
		var modifier = new Writer("invalid-writer-id", "olleh");

		given(this.boardService.findBoardEntityById(service, boardId)).willReturn(boardEntity);
		given(this.articleHeaderRepository.findArticleWriterById(service, id)).willReturn(modifier);

		// when, then
		assertThatThrownBy(() -> this.articleService.modifyArticle(service, id, articleModifyRequest))
			.isInstanceOf(ArticleErrors.ArticleModifyException.class);
	}

	@Test
	void deleteArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setBoardId(boardId);
		headerEntity.setActionDateTime(new ActionDateTime());
		headerEntity.getActionDateTime().setDeletedAt(ZonedDateTime.now());
		headerEntity.setState(ArticleState.Deleted.getCode());

		given(this.articleHeaderRepository.findArticleHeaderById(service, id, true)).willReturn(headerEntity);
		given(this.articleHeaderRepository.deleteArticle(service, id)).willReturn(headerEntity);

		// when
		var deleteResult = this.articleService.deleteArticle(service, id);

		// then
		assertThat(deleteResult).isNotNull();
		assertThat(deleteResult.id()).isEqualTo(id);
		assertThat(deleteResult.state()).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(deleteResult.deletedAt()).isNotNull();
	}

	@Test
	void deleteArticleShouldFail() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setId(id);
		headerEntity.setBoardId(boardId);
		headerEntity.setActionDateTime(new ActionDateTime());
		headerEntity.getActionDateTime().setDeletedAt(ZonedDateTime.now());
		headerEntity.setState(ArticleState.Deleted.getCode());

		given(this.articleHeaderRepository.findArticleHeaderById(service, id, true)).willReturn(null);

		// when, then
		assertThatThrownBy(() -> this.articleService.deleteArticle(service, id))
			.isInstanceOf(ArticleErrors.ArticleDeleteException.class);
	}

}
