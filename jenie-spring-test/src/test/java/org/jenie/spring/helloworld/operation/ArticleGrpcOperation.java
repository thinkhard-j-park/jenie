package org.jenie.spring.helloworld.operation;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderMessage;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ArticleRequestMessage;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.grpc.ArticleServiceGrpc;

public class ArticleGrpcOperation implements ArticleOperation {

	private final ArticleServiceGrpc.ArticleServiceBlockingStub blockingStub;

	public ArticleGrpcOperation(ArticleServiceGrpc.ArticleServiceBlockingStub blockingStub) {
		this.blockingStub = blockingStub;
	}

	@Override
	public Article writeArticle(String service, ArticleRequest articleRequest) {
		return null;
	}

	@Override
	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		return null;
	}

	@Override
	public Article modifyArticle(String service, String articleId, ArticleRequest modifyRequest) {
		return null;
	}

	@Override
	public Article viewArticle(String service, String articleId, boolean incViewCount) {
		return null;
	}

	@Override
	public ArticleHeader getArticleByHeader(String service, String articleId, boolean latest) {
		var req = ArticleRequestMessage.newBuilder().setService(service).setId(articleId).setLatest(latest).build();
		ArticleHeaderMessage articleHeaderMessage = this.blockingStub.getArticleHeaderById(req);
		return ArticleHeader.fromProtoMessage(articleHeaderMessage);
	}

	@Override
	public ArticleDeleteResult deleteArticle(String service, String articleId) {
		return null;
	}

}
