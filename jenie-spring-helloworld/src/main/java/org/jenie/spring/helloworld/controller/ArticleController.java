package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.entity.ArticleHeader;
import org.jenie.spring.helloworld.service.ArticleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{service}/article")
public class ArticleController {

	private final ArticleService articleService;

	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@GetMapping("/{id}")
	public ArticleHeader getArticleHeaderById(@PathVariable String service, @PathVariable String id) {
		return this.articleService.getArticleHeaderById(service, id);
	}
	/**
	 * TODO Rest Web App 게시글 목록 보기 스키마 설계 article-header article-content
	 *
	 ** Problem Detail 정적 테스트
	 *
	 ** 통합 테스트
	 *
	 */

}
