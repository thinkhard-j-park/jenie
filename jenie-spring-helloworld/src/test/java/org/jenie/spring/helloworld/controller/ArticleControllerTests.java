package org.jenie.spring.helloworld.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.SortCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTests {

	@InjectMocks
	private ArticleController articleController;

	@Mock
	private ArticleService articleService;

	@Test
	void getArticleHeaderById() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var header = ArticleHeader.builder().id(id).board(Board.builder().id(boardId).build()).build();
		given(articleService.getArticleHeaderById(service, id, false)).willReturn(header);

		// when
		var fetchedHeader = articleController.getArticleHeaderById(service, id, false);

		// then
		assertThat(fetchedHeader).isNotNull();
		assertThat(fetchedHeader.id()).isEqualTo(id);
		assertThat(fetchedHeader.board()).isNotNull();
		assertThat(fetchedHeader.board().id()).isNotNull();
	}

	@Test
	void viewArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var id = "article-id";
		var header = ArticleHeader.builder().id(id).board(Board.builder().id(boardId).build()).build();
		var content = "content";
		var article = new Article(header, content);
		given(articleService.viewArticle(service, id, false)).willReturn(article);

		// when
		var fetchedArticle = articleController.viewArticle(service, id, false);

		// then
		assertThat(fetchedArticle).isNotNull();
		assertThat(fetchedArticle.header()).isNotNull();
		assertThat(fetchedArticle.header().id()).isEqualTo(id);
		assertThat(fetchedArticle.header().board()).isNotNull();
		assertThat(fetchedArticle.header().board().id()).isNotNull();
		assertThat(fetchedArticle.content()).isEqualTo(content);
	}

	@Test
	void listArticleHeader() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var param = new ListArticleHeaderRequestParam(boardId, "", 5, SortCode.TIME_DESC.getCode());
		var articleHeaderList = ArticleHeaderList.from(List.of(ArticleHeader.builder().build()), 1);
		given(articleService.listArticleHeader(service, param)).willReturn(articleHeaderList);

		// when
		var fetchedArticleHeaderList = articleController.listArticleHeader(service, param);

		// then
		assertThat(fetchedArticleHeaderList).isNotNull();
		assertThat(fetchedArticleHeaderList.list()).hasSize(1);
		assertThat(fetchedArticleHeaderList.hasMore()).isFalse();
	}

	@Test
	void writeArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		var header = ArticleHeader.builder()
			.id("article-id")
			.board(Board.builder().id(boardId).build())
			.title(title)
			.writer(writer)
			.build();
		var article = new Article(header, content);
		given(articleService.writeArticle(service, articleRequest)).willReturn(article);

		// when
		var createdArticle = articleController.writeArticle(service, articleRequest);

		// then
		assertThat(createdArticle).isNotNull();
		assertThat(createdArticle.header()).isNotNull();
		assertThat(createdArticle.header().id()).isEqualTo("article-id");
		assertThat(createdArticle.header().board()).isNotNull();
		assertThat(createdArticle.header().board().id()).isEqualTo(boardId);
		assertThat(createdArticle.header().title()).isEqualTo(title);
		assertThat(createdArticle.header().writer()).isNotNull();
		assertThat(createdArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(createdArticle.content()).isEqualTo(content);
	}

	@Test
	void modifyArticle() {
		// given
		var service = "jenie-test";
		var boardId = "board-id";
		var articleId = "article-id";
		var title = "hello world";
		var writer = new Writer("writer-id", "olleh");
		var content = "here i am";
		var articleRequest = new ArticleRequest(boardId, title, content, writer);

		var header = ArticleHeader.builder()
			.id("article-id")
			.board(Board.builder().id(boardId).build())
			.title(title)
			.writer(writer)
			.build();
		var article = new Article(header, content);
		given(articleService.modifyArticle(service, articleId, articleRequest)).willReturn(article);

		// when
		var modifiedArticle = articleController.modifyArticle(service, articleId, articleRequest);

		// then
		assertThat(modifiedArticle).isNotNull();
		assertThat(modifiedArticle.header()).isNotNull();
		assertThat(modifiedArticle.header().id()).isEqualTo(articleId);
		assertThat(modifiedArticle.header().board()).isNotNull();
		assertThat(modifiedArticle.header().board().id()).isEqualTo(boardId);
		assertThat(modifiedArticle.header().title()).isEqualTo(title);
		assertThat(modifiedArticle.header().writer()).isNotNull();
		assertThat(modifiedArticle.header().writer().getWid()).isEqualTo(writer.getWid());
		assertThat(modifiedArticle.content()).isEqualTo(content);
	}

	@Test
	void deleteArticle() {
		// given
		var service = "jenie-test";
		var id = "article-id";
		var articleDeleteResult = new ArticleDeleteResult(id, ArticleState.Deleted.getCode(), ZonedDateTime.now());
		given(articleService.deleteArticle(service, id)).willReturn(articleDeleteResult);

		// when
		var deleteResult = articleController.deleteArticle(service, id);

		// then
		assertThat(deleteResult).isNotNull();
		assertThat(deleteResult.id()).isEqualTo(id);
		assertThat(deleteResult.state()).isEqualTo(ArticleState.Deleted.getCode());
		assertThat(deleteResult.deletedAt()).isEqualTo(articleDeleteResult.deletedAt());
	}

}
