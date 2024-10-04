package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleDeleteResult;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.article.ArticleHeaderList;
import org.jenie.spring.helloworld.dto.article.ArticleRequest;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.service.ArticleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{service}/article")
public class ArticleController {

	private final ArticleService articleService;

	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@GetMapping("/{id}/header")
	public ArticleHeader getArticleHeaderById(@PathVariable String service, @PathVariable String id,
			@RequestParam(defaultValue = "false") boolean latest) {
		return this.articleService.getArticleHeaderById(service, id, latest);
	}

	@GetMapping("/{id}")
	public Article viewArticle(@PathVariable String service, @PathVariable String id,
			@RequestParam(defaultValue = "true") boolean incViewCount) {
		return null;
	}

	@GetMapping("/list")
	public ArticleHeaderList listArticleHeader(@PathVariable String service, ListArticleHeaderRequestParam param) {
		return this.articleService.listArticleHeader(service, param);
	}

	@PostMapping
	public Article writeArticle(@PathVariable String service, @RequestBody ArticleRequest articleRequest) {
		return this.articleService.writeArticle(service, articleRequest);
	}

	@PutMapping("/{id}")
	public Article modifyArticle(@PathVariable String service, @PathVariable String id,
			@RequestBody ArticleRequest articleRequest) {
		return this.articleService.modifyArticle(service, id, articleRequest);
	}

	@DeleteMapping("/{id}")
	public ArticleDeleteResult deleteArticle(@PathVariable String service, @PathVariable String id) {
		return null;
	}

	/**
	 * TODO crud article,
	 */

}
