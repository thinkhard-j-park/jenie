package org.jenie.spring.helloworld.operation;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;

public interface ArticleOperation {

	Article writeArticle(String service, ArticleRequest articleRequest);

	ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param);

	Article modifyArticle(String service, String articleId, ArticleRequest modifyRequest);

	Article viewArticle(String service, String articleId, boolean incViewCount);

	ArticleHeader getArticleByHeader(String service, String articleId, boolean latest);

	ArticleDeleteResult deleteArticle(String service, String articleId);

}
