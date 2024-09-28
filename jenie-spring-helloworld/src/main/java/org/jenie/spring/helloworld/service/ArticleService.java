package org.jenie.spring.helloworld.service;

import org.jenie.spring.data.mongodb.transaction.DBKey;
import org.jenie.spring.data.mongodb.transaction.MongoKeyBasedTransactional;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.BoardException;
import org.jenie.spring.helloworld.exception.ErrorCode;
import org.jenie.spring.helloworld.mapper.ArticleHeaderMapper;
import org.jenie.spring.helloworld.repository.ArticleContentRepository;
import org.jenie.spring.helloworld.repository.ArticleHeaderRepository;

import org.springframework.stereotype.Service;

@Service
public class ArticleService {

	private final BoardService boardService;

	private final ArticleHeaderRepository articleHeaderRepository;

	private final ArticleContentRepository articleContentRepository;

	public ArticleService(BoardService boardService, ArticleHeaderRepository articleHeaderRepository,
			ArticleContentRepository articleContentRepository) {
		this.boardService = boardService;
		this.articleHeaderRepository = articleHeaderRepository;
		this.articleContentRepository = articleContentRepository;
	}

	public ArticleHeader getArticleHeaderById(String service, String id) {
		return ArticleHeaderMapper.toDto(this.articleHeaderRepository.findArticleHeaderById(service, id));
	}

	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		var list = this.articleHeaderRepository.listArticleHeader(service, param)
			.stream()
			.map((articleEntity) -> ArticleHeaderMapper.toDto(articleEntity,
					this.boardService.findBoardEntityById(service, articleEntity.getBoardId())))
			.toList();

		return ArticleHeaderList.from(list, param.size());
	}

	@MongoKeyBasedTransactional
	public Article writeArticle(@DBKey String service, ArticleRequest articleRequest) {
		var board = this.boardService.findBoardEntityById(service, articleRequest.boardId());
		if (board == null) {
			throw new BoardException.BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND,
					"Failed to write article because board was not found: " + service + ", "
							+ articleRequest.boardId());
		}

		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setBoardId(articleRequest.boardId());
		headerEntity.setTitle(articleRequest.title());
		headerEntity.setWriter(articleRequest.writer());
		var contentEntity = new ArticleContentEntity();
		contentEntity.setContent(articleRequest.content());

		var savedHeaderEntity = this.articleHeaderRepository.insert(service, headerEntity);
		var savedContentEntity = this.articleContentRepository.insert(service, contentEntity);
		var header = ArticleHeaderMapper.toDto(savedHeaderEntity, board);
		var content = savedContentEntity.getContent();

		return new Article(header, content);
	}

}
