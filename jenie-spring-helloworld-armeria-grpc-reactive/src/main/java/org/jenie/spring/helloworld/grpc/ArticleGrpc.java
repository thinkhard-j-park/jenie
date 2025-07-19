package org.jenie.spring.helloworld.grpc;

import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResultMessage;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderListMessage;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderMessage;
import org.jenie.spring.helloworld.dto.article.ArticleMessage;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.reactive.service.ReactiveArticleService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Service;

@Service
public class ArticleGrpc extends ReactorArticleServiceGrpc.ArticleServiceImplBase {

	private final ReactiveArticleService articleService;

	public ArticleGrpc(ReactiveArticleService articleService) {
		this.articleService = articleService;
	}

	@Override
	public Mono<ArticleHeaderMessage> getArticleHeaderById(GetArticleHeaderByIdRequestMessage request) {
		return this.articleService.getArticleHeaderById(request.getService(), request.getId(), request.getLatest())
			.map(ArticleHeader::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<ArticleMessage> viewArticle(ViewArticleRequestMessage request) {
		return this.articleService.viewArticle(request.getService(), request.getId(), request.getIncViewCount())
			.map(Article::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<ArticleHeaderListMessage> listArticleHeader(ListArticleHeaderRequestMessage request) {
		var service = request.getService();
		var param = new ListArticleHeaderRequestParam(request.getBoardId(), request.getPrevArticleId(),
				request.getSize(), request.getSort());
		return this.articleService.listArticleHeader(service, param)
			.map(ArticleHeaderList::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<ArticleMessage> writeArticle(WriteArticleRequestMessage request) {
		var articleRequest = new ArticleRequest(request.getBoardId(), request.getTitle(), request.getContent(),
				Writer.fromProtoMessage(request.getWriter()));

		return this.articleService.writeArticle(request.getService(), articleRequest)
			.map(Article::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<ArticleMessage> modifyArticle(ModifyArticleRequestMessage request) {
		var articleRequest = new ArticleRequest(request.getBoardId(), request.getTitle(), request.getContent(),
				Writer.fromProtoMessage(request.getWriter()));

		return this.articleService.modifyArticle(request.getService(), request.getId(), articleRequest)
			.map(Article::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<ArticleDeleteResultMessage> deleteArticle(DeleteArticleRequestMessage request) {
		return this.articleService.deleteArticle(request.getService(), request.getId())
			.map(ArticleDeleteResult::toProtoMessage)
			.subscribeOn(Schedulers.boundedElastic())
			.publishOn(Schedulers.boundedElastic());
	}

}
