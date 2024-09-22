package org.jenie.spring.helloworld.service;

import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.data.mongodb.transaction.DBKey;
import org.jenie.spring.data.mongodb.transaction.MongoKeyBasedTransactional;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.article.ArticleContentEntity;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.mapper.ArticleHeaderMapper;
import org.jenie.spring.helloworld.repository.ArticleContentRepository;
import org.jenie.spring.helloworld.repository.ArticleHeaderRepository;

import org.springframework.stereotype.Service;

@Service
public class ArticleService {

	private final MongoTemplateRouter mongoTemplateRouter;

	private final ArticleHeaderRepository articleHeaderRepository;

	private final ArticleContentRepository articleContentRepository;

	public ArticleService(MongoTemplateRouter mongoTemplateRouter, ArticleHeaderRepository articleHeaderRepository,
			ArticleContentRepository articleContentRepository) {
		this.mongoTemplateRouter = mongoTemplateRouter;
		this.articleHeaderRepository = articleHeaderRepository;
		this.articleContentRepository = articleContentRepository;
	}

	public ArticleHeader getArticleHeaderById(String service, String id) {
		return ArticleHeaderMapper.INSTANCE.toDto(this.articleHeaderRepository.findById(service, id));
	}

	public ArticleHeaderList listArticleHeader(String service, ListArticleHeaderRequestParam param) {
		var list = this.articleHeaderRepository.listArticleHeader(service, param)
			.stream()
			.map(ArticleHeaderMapper.INSTANCE::toDto)
			.toList();

		return ArticleHeaderList.from(list, param.size());
	}

	@MongoKeyBasedTransactional
	public Article writeArticle(@DBKey String service, ArticleRequest articleRequest) {
		// TODO boardId 가 올바른지 체크
		final var template = this.mongoTemplateRouter.txMongoTemplate(service);

		var headerEntity = new ArticleHeaderEntity();
		headerEntity.setBoardId(articleRequest.boardId());
		headerEntity.setTitle(articleRequest.title());
		headerEntity.setWriter(articleRequest.writer());

		var contentEntity = new ArticleContentEntity();
		contentEntity.setContent(articleRequest.content());

		var savedHeaderEntity = this.articleHeaderRepository.insert(template, headerEntity);

		var savedContentEntity = this.articleContentRepository.insert(template, contentEntity);
		var header = ArticleHeaderMapper.INSTANCE.toDto(savedHeaderEntity);
		var content = savedContentEntity.getContent();

		return new Article(header, content);
	}

}
