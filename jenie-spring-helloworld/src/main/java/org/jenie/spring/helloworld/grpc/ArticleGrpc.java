package org.jenie.spring.helloworld.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderMessage;
import org.jenie.spring.helloworld.dto.article.ArticleRequestMessage;
import org.jenie.spring.helloworld.service.ArticleService;

@GrpcService
public class ArticleGrpc extends ArticleServiceGrpc.ArticleServiceImplBase {

	private final ArticleService articleService;

	public ArticleGrpc(ArticleService articleService) {
		this.articleService = articleService;
	}

	@Override
	public void getArticleHeaderById(ArticleRequestMessage request,
			StreamObserver<ArticleHeaderMessage> responseObserver) {
		var articleHeader = this.articleService.getArticleHeaderById(request.getService(), request.getId(),
				request.getLatest());

		emitResponse(responseObserver, ArticleHeader.toProtoMessage(articleHeader));
	}

	private <T> void emitResponse(StreamObserver<T> responseObserver, T message) {
		responseObserver.onNext(message);
		responseObserver.onCompleted();
	}

}
