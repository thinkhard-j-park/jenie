package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.service.ArticleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	@GetMapping("/list")
	public ArticleHeaderList listArticleHeaders(@PathVariable String service,
			@ModelAttribute ListArticleHeaderRequestParam param) {

		return this.articleService.listArticleHeader(service, param);
	}

	/**
	 * TODO crud article,
	 *
	 ** Problem Detail 정적 테스트
	 *
	 ** 통합 테스트
	 *
	 */

}
