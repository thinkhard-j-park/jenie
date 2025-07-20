package org.jenie.spring.helloworld.reactive.service;

import org.jenie.spring.data.mongodb.transaction.DBKey;
import org.jenie.spring.data.mongodb.transaction.MongoKeyBasedTransactional;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.exception.ArticleErrors;
import org.jenie.spring.helloworld.exception.BoardErrors;
import org.jenie.spring.helloworld.mapper.ArticleHeaderMapper;
import org.jenie.spring.helloworld.reactive.repository.ReactiveArticleContentRepository;
import org.jenie.spring.helloworld.reactive.repository.ReactiveArticleHeaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Service;

@ConditionalOnReactive
@Service
public class ReactiveArticleService {

	private final ReactiveBoardService boardService;

	private final ReactiveArticleHeaderRepository articleHeaderRepository;

	private final ReactiveArticleContentRepository articleContentRepository;

	private static final Logger logger = LoggerFactory.getLogger(ReactiveArticleService.class);

	public ReactiveArticleService(ReactiveBoardService boardService,
			ReactiveArticleHeaderRepository articleHeaderRepository,
			ReactiveArticleContentRepository articleContentRepository) {
		this.boardService = boardService;
		this.articleHeaderRepository = articleHeaderRepository;
		this.articleContentRepository = articleContentRepository;
	}

	public Mono<ArticleHeader> getArticleHeaderById(String service, String id, boolean latest) {
		return this.articleHeaderRepository.findArticleHeaderById(service, id, latest)
			.map(ArticleHeaderMapper::toDto)
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<ArticleHeaderList> listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		return this.articleHeaderRepository.listArticleHeader(service, param)
			.flatMap((articleEntity) -> this.boardService.findBoardEntityById(service, articleEntity.getBoardId())
				.map((boardEntity) -> ArticleHeaderMapper.toDto(articleEntity, boardEntity)))
			.collectList()
			.map((list) -> ArticleHeaderList.from(list, param.getSize()))
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<Article> viewArticle(String service, String id, boolean incViewCount) {
		return Mono
			.zip(this.articleHeaderRepository.findArticleHeaderById(service, id, false),
					this.articleContentRepository.findArticleContentById(service, id))
			.flatMap((t2) -> {
				var headerEntity = t2.getT1();
				var contentEntity = t2.getT2();
				return this.boardService.findBoardEntityById(service, headerEntity.getBoardId())
					.flatMap((boardEntity) -> {
						var header = ArticleHeaderMapper.toDto(headerEntity, boardEntity);
						var content = contentEntity.getContent();

						if (incViewCount) {
							return this.articleHeaderRepository.incViewCount(service, id, 1)
								.then(Mono.just(new Article(header, content)));
						}
						return Mono.just(new Article(header, content));
					});
			})
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

	@MongoKeyBasedTransactional
	public Mono<Article> writeArticle(@DBKey String service, ArticleRequest articleRequest) {
		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setBoardId(articleRequest.boardId());
		headerEntity.setTitle(articleRequest.title());
		headerEntity.setWriter(articleRequest.writer());

		return this.articleHeaderRepository.insert(service, headerEntity).zipWhen((savedHeaderEntity) -> {
			var contentEntity = new ArticleContentEntity();
			contentEntity.setContent(articleRequest.content());
			contentEntity.setId(savedHeaderEntity.getId());

			return this.articleContentRepository.insert(service, contentEntity);
		}).flatMap((t2) -> {
			var savedHeaderEntity = t2.getT1();
			var savedContentEntity = t2.getT2();
			return this.getBoardEntity(service, articleRequest.boardId()).map((boardEntity) -> {
				var header = ArticleHeaderMapper.toDto(savedHeaderEntity, boardEntity);
				var content = savedContentEntity.getContent();

				return new Article(header, content);
			});

		}).doOnError((error) -> logger.error(error.getMessage(), error)).subscribeOn(Schedulers.boundedElastic());
	}

	private Mono<BoardEntity> getBoardEntity(String service, String boardId) {
		return this.boardService.findBoardEntityById(service, boardId)
			.switchIfEmpty(Mono.error(new BoardErrors.BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND,
					"Failed to write article because board was not found: " + service + ", " + boardId)))
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

	@MongoKeyBasedTransactional
	public Mono<Article> modifyArticle(@DBKey String service, String id, ArticleRequest modifyRequest) {
		return this.articleHeaderRepository.findArticleWriterById(service, id)
			.switchIfEmpty(Mono.error(new ArticleErrors.ArticleNotFoundException(ErrorCode.ARTICLE_NOT_FOUND,
					"Failed to get article writer: " + service + ", " + id)))
			.flatMap((writer) -> {
				if (!writer.getWid().equals(modifyRequest.writer().getWid())) {
					return Mono.error(new ArticleErrors.ArticleModifyException(ErrorCode.ARTICLE_MODIFY_NOT_ALLOWED,
							"This user is not allowed to modify this article: " + service + ", " + id + ", "
									+ writer.getWid()));
				}
				return Mono.just(writer);
			})
			.then(Mono.zip(this.articleHeaderRepository.modifyArticleHeader(service, id, modifyRequest.title()),
					this.articleContentRepository.modifyArticleContent(service, id, modifyRequest.content()),
					this.getBoardEntity(service, modifyRequest.boardId())))
			.map((t3) -> {
				var modifiedArticleHeader = t3.getT1();
				var modifiedArticleContent = t3.getT2();
				var boardEntity = t3.getT3();
				var header = ArticleHeaderMapper.toDto(modifiedArticleHeader, boardEntity);
				var content = modifiedArticleContent.getContent();
				return new Article(header, content);
			})
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<ArticleDeleteResult> deleteArticle(String service, String id) {
		return this.articleHeaderRepository.findArticleHeaderById(service, id, true)
			.switchIfEmpty(Mono.error(new ArticleErrors.ArticleDeleteException(ErrorCode.ARTICLE_NOT_FOUND,
					"There is no article to delete: " + service + ", " + id)))
			.then(this.articleHeaderRepository.deleteArticle(service, id))
			.map((deleted) -> new ArticleDeleteResult(deleted.getId(), deleted.getState(),
					deleted.getActionDateTime().getDeletedAt()))
			.doOnError((error) -> logger.error(error.getMessage(), error))
			.subscribeOn(Schedulers.boundedElastic());
	}

}
