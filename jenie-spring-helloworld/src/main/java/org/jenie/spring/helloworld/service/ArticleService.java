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
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.ArticleErrors;
import org.jenie.spring.helloworld.exception.BoardErrors;
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

	public ArticleHeader getArticleHeaderById(String service, String id, boolean latest) {
		return ArticleHeaderMapper.toDto(this.articleHeaderRepository.findArticleHeaderById(service, id, latest));
	}

	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		var list = this.articleHeaderRepository.listArticleHeader(service, param)
			.stream()
			.map((articleEntity) -> ArticleHeaderMapper.toDto(articleEntity,
					this.boardService.findBoardEntityById(service, articleEntity.getBoardId())))
			.toList();

		return ArticleHeaderList.from(list, param.size());
	}

	public Article viewArticle(String service, String id, boolean incViewCount) {
		var headerEntity = this.articleHeaderRepository.findArticleHeaderById(service, id, false);
		var contentEntity = this.articleContentRepository.findArticleContentById(service, id);
		var boardEntity = this.boardService.findBoardEntityById(service, headerEntity.getBoardId());

		var header = ArticleHeaderMapper.toDto(headerEntity, boardEntity);
		var content = contentEntity.getContent();

		if (incViewCount) {
			this.articleHeaderRepository.incViewCountAsync(service, id, 1);
		}

		return new Article(header, content);
	}

	@MongoKeyBasedTransactional
	public Article writeArticle(@DBKey String service, ArticleRequest articleRequest) {
		var board = this.getBoardEntity(service, articleRequest.boardId());

		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setBoardId(articleRequest.boardId());
		headerEntity.setTitle(articleRequest.title());
		headerEntity.setWriter(articleRequest.writer());

		var contentEntity = new ArticleContentEntity();
		contentEntity.setContent(articleRequest.content());

		var savedHeaderEntity = this.articleHeaderRepository.insert(service, headerEntity);

		contentEntity.setId(savedHeaderEntity.getId());
		var savedContentEntity = this.articleContentRepository.insert(service, contentEntity);

		var header = ArticleHeaderMapper.toDto(savedHeaderEntity, board);
		var content = savedContentEntity.getContent();

		return new Article(header, content);
	}

	private BoardEntity getBoardEntity(String service, String boardId) {
		var board = this.boardService.findBoardEntityById(service, boardId);
		if (board == null) {
			throw new BoardErrors.BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND,
					"Failed to write article because board was not found: " + service + ", " + boardId);
		}

		return board;
	}

	@MongoKeyBasedTransactional
	public Article modifyArticle(@DBKey String service, String id, ArticleRequest modifyRequest) {
		var board = this.getBoardEntity(service, modifyRequest.boardId());
		var writer = this.articleHeaderRepository.findArticleWriterById(service, id);
		if (writer == null) {
			throw new ArticleErrors.ArticleNotFoundException(ErrorCode.ARTICLE_NOT_FOUND,
					"Failed to get article writer: " + service + ", " + id);
		}

		if (!writer.getWid().equals(modifyRequest.writer().getWid())) {
			throw new ArticleErrors.ArticleModifyException(ErrorCode.ARTICLE_MODIFY_NOT_ALLOWED,
					"This user is not allowed to modify this article: " + service + ", " + id + ", " + writer.getWid());
		}

		var modifiedArticleHeader = this.articleHeaderRepository.modifyArticleHeader(service, id,
				modifyRequest.title());
		var modifiedArticleContent = this.articleContentRepository.modifyArticleContent(service, id,
				modifyRequest.content());
		var header = ArticleHeaderMapper.toDto(modifiedArticleHeader, board);
		var content = modifiedArticleContent.getContent();

		return new Article(header, content);
	}

}
