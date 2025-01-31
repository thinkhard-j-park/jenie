package org.jenie.spring.helloworld.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
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
import org.jenie.spring.helloworld.service.sync.ArticleService;

@GrpcService
public class ArticleGrpc extends ArticleServiceGrpc.ArticleServiceImplBase {

	private final ArticleService articleService;

	public ArticleGrpc(ArticleService articleService) {
		this.articleService = articleService;
	}

	private <T> void emitResponse(StreamObserver<T> responseObserver, T message) {
		responseObserver.onNext(message);
		responseObserver.onCompleted();
	}

	@Override
	public void getArticleHeaderById(GetArticleHeaderByIdRequestMessage request,
			StreamObserver<ArticleHeaderMessage> responseObserver) {
		var articleHeader = this.articleService.getArticleHeaderById(request.getService(), request.getId(),
				request.getLatest());

		emitResponse(responseObserver, ArticleHeader.toProtoMessage(articleHeader));
	}

	@Override
	public void viewArticle(ViewArticleRequestMessage request, StreamObserver<ArticleMessage> responseObserver) {
		var article = this.articleService.viewArticle(request.getService(), request.getId(), request.getIncViewCount());

		emitResponse(responseObserver, Article.toProtoMessage(article));
	}

	@Override
	public void listArticleHeader(ListArticleHeaderRequestMessage request,
			StreamObserver<ArticleHeaderListMessage> responseObserver) {
		var param = new ListArticleHeaderRequestParam(request.getBoardId(), request.getPrevArticleId(),
				request.getSize(), request.getSort());
		var articleHeaderList = this.articleService.listArticleHeader(request.getService(), param);

		emitResponse(responseObserver, ArticleHeaderList.toProtoMessage(articleHeaderList));
	}

	@Override
	public void writeArticle(WriteArticleRequestMessage request, StreamObserver<ArticleMessage> responseObserver) {
		var articleRequest = new ArticleRequest(request.getBoardId(), request.getTitle(), request.getContent(),
				Writer.fromProtoMessage(request.getWriter()));

		var article = this.articleService.writeArticle(request.getService(), articleRequest);
		emitResponse(responseObserver, Article.toProtoMessage(article));
	}

	@Override
	public void modifyArticle(ModifyArticleRequestMessage request, StreamObserver<ArticleMessage> responseObserver) {
		var articleRequest = new ArticleRequest(request.getBoardId(), request.getTitle(), request.getContent(),
				Writer.fromProtoMessage(request.getWriter()));

		var article = this.articleService.modifyArticle(request.getService(), request.getId(), articleRequest);
		emitResponse(responseObserver, Article.toProtoMessage(article));
	}

	@Override
	public void deleteArticle(DeleteArticleRequestMessage request,
			StreamObserver<ArticleDeleteResultMessage> responseObserver) {
		var deleteResult = this.articleService.deleteArticle(request.getService(), request.getId());
		emitResponse(responseObserver, ArticleDeleteResult.toProtoMessage(deleteResult));
	}

}
