package org.jenie.spring.helloworld.service;

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

	private final ArticleHeaderRepository articleHeaderRepository;

	private final ArticleContentRepository articleContentRepository;

	public ArticleService(ArticleHeaderRepository articleHeaderRepository,
			ArticleContentRepository articleContentRepository) {
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

	public Article writeArticle(String service, ArticleRequest articleRequest) {
		// TODO boardId 가 올바른지 체크
		// TODO 트랙잰션 처리
		var header = new ArticleHeaderEntity();
		header.setBoardId(articleRequest.boardId());
		header.setTitle(articleRequest.title());
		header.setWriter(articleRequest.writer());

		var content = new ArticleContentEntity();
		content.setContent(articleRequest.content());

		this.articleHeaderRepository.create(header);
		this.articleContentRepository.upsert(content);
	}

}
