package org.jenie.spring.helloworld.service;

import org.jenie.spring.helloworld.entity.ArticleHeader;
import org.jenie.spring.helloworld.repository.ArticleHeaderRepository;

import org.springframework.stereotype.Service;

@Service
public class ArticleService {

	private final ArticleHeaderRepository articleHeaderRepository;

	public ArticleService(ArticleHeaderRepository articleHeaderRepository) {
		this.articleHeaderRepository = articleHeaderRepository;
	}

	public ArticleHeader getArticleHeaderById(String service, String id) {
		return this.articleHeaderRepository.findById(service, id);
	}

}
