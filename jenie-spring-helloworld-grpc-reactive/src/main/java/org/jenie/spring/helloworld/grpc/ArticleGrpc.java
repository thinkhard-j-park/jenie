package org.jenie.spring.helloworld.grpc;

import net.devh.boot.grpc.server.service.GrpcService;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderListMessage;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderMessage;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.reactive.service.ReactiveArticleService;
import reactor.core.publisher.Mono;

@GrpcService
public class ArticleGrpc extends ReactorArticleServiceGrpc.ArticleServiceImplBase {

	private final ReactiveArticleService articleService;

	public ArticleGrpc(ReactiveArticleService articleService) {
		this.articleService = articleService;
	}

	@Override
	public Mono<ArticleHeaderMessage> getArticleHeaderById(GetArticleHeaderByIdRequestMessage request) {
		return this.articleService.getArticleHeaderById(request.getService(), request.getId(), request.getLatest())
			.map(ArticleHeader::toProtoMessage);
	}

	@Override
	public Mono<ArticleHeaderListMessage> listArticleHeader(ListArticleHeaderRequestMessage request) {
		var service = request.getService();
		var param = new ListArticleHeaderRequestParam(request.getBoardId(), request.getPrevArticleId(),
				request.getSize(), request.getSort());
		return this.articleService.listArticleHeader(service, param).map(ArticleHeaderList::toProtoMessage);
	}

}
