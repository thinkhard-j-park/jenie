package org.jenie.spring.helloworld.service;

import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.mapper.ArticleHeaderMapper;
import org.jenie.spring.helloworld.repository.ArticleHeaderRepository;

import org.springframework.stereotype.Service;

@Service
public class ArticleService {

	private final ArticleHeaderRepository articleHeaderRepository;

	public ArticleService(ArticleHeaderRepository articleHeaderRepository) {
		this.articleHeaderRepository = articleHeaderRepository;
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

}
