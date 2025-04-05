package org.jenie.spring.helloworld.operation;

import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.grpc.ArticleServiceGrpc;
import org.jenie.spring.helloworld.grpc.DeleteArticleRequestMessage;
import org.jenie.spring.helloworld.grpc.GetArticleHeaderByIdRequestMessage;
import org.jenie.spring.helloworld.grpc.ListArticleHeaderRequestMessage;
import org.jenie.spring.helloworld.grpc.ModifyArticleRequestMessage;
import org.jenie.spring.helloworld.grpc.ViewArticleRequestMessage;
import org.jenie.spring.helloworld.grpc.WriteArticleRequestMessage;

public class ArticleGrpcOperation implements ArticleOperation {

	private final ArticleServiceGrpc.ArticleServiceBlockingStub stub;

	public ArticleGrpcOperation(ArticleServiceGrpc.ArticleServiceBlockingStub stub) {
		this.stub = stub;
	}

	@Override
	public Article writeArticle(String service, ArticleRequest articleRequest) {
		var requestMessage = WriteArticleRequestMessage.newBuilder()
			.setService(service)
			.setBoardId(articleRequest.boardId())
			.setTitle(articleRequest.title())
			.setContent(articleRequest.content())
			.setWriter(Writer.toProtoMessage(articleRequest.writer()))
			.build();
		var articleMessage = this.stub.writeArticle(requestMessage);
		return Article.fromProtoMessage(articleMessage);
	}

	@Override
	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		var requestMessage = ListArticleHeaderRequestMessage.newBuilder()
			.setService(service)
			.setBoardId(param.getBoardId())
			.setPrevArticleId(param.getPrevArticleId())
			.setSize(param.getSize())
			.setSort(param.getSort())
			.build();
		var articleHeaderListMessage = this.stub.listArticleHeader(requestMessage);
		return ArticleHeaderList.fromProtoMessage(articleHeaderListMessage);
	}

	@Override
	public Article modifyArticle(String service, String articleId, ArticleRequest modifyRequest) {
		var requestMessage = ModifyArticleRequestMessage.newBuilder()
			.setService(service)
			.setId(articleId)
			.setBoardId(modifyRequest.boardId())
			.setTitle(modifyRequest.title())
			.setContent(modifyRequest.content())
			.setWriter(Writer.toProtoMessage(modifyRequest.writer()))
			.build();
		var article = this.stub.modifyArticle(requestMessage);
		return Article.fromProtoMessage(article);
	}

	@Override
	public Article viewArticle(String service, String articleId, boolean incViewCount) {
		var requestMessage = ViewArticleRequestMessage.newBuilder()
			.setService(service)
			.setId(articleId)
			.setIncViewCount(incViewCount)
			.build();
		var articleMessage = this.stub.viewArticle(requestMessage);
		return Article.fromProtoMessage(articleMessage);
	}

	@Override
	public ArticleHeader getArticleByHeader(String service, String articleId, boolean latest) {
		var requestMessage = GetArticleHeaderByIdRequestMessage.newBuilder()
			.setService(service)
			.setId(articleId)
			.setLatest(latest)
			.build();
		var articleHeaderMessage = this.stub.getArticleHeaderById(requestMessage);
		return ArticleHeader.fromProtoMessage(articleHeaderMessage);
	}

	@Override
	public ArticleDeleteResult deleteArticle(String service, String articleId) {
		var requestMessage = DeleteArticleRequestMessage.newBuilder().setService(service).setId(articleId).build();
		var deleteResultMessage = this.stub.deleteArticle(requestMessage);
		return ArticleDeleteResult.fromProtoMessage(deleteResultMessage);
	}

}
