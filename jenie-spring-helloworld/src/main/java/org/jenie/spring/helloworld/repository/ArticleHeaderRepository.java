package org.jenie.spring.helloworld.repository;

import java.time.ZonedDateTime;
import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.jenie.spring.helloworld.dto.article.ListArticleHeaderRequestParam;
import org.jenie.spring.helloworld.entity.SortOrder;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.exception.AssertHelper;
import org.jenie.spring.helloworld.pojo.ActionDateTime;
import org.jenie.spring.helloworld.pojo.Writer;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ArticleHeaderRepository extends MongoDBRepository {

	public ArticleHeaderRepository(MongoTemplateRouter mongoTemplateRouter) {
		super(mongoTemplateRouter);
	}

	/**
	 * ArticleHeader 을 조회한다.
	 * @param dbKey '클러스터-데이터베이스' 을 찾는 키
	 * @param id 게시글 아이디
	 * @param latest true 로 지정된 경우 Primary 에서 데이터를 읽는다. 그렇지 않는 경우는 SecondaryPreferred 을
	 * 사용한다. SecondaryPreferred 을 사용하는 경우 ReplicaSet 동기화 시간 동안 최신 데이터가 아닐 수 있다.
	 * @return db 에서 읽어온 ArticleHeader 데이터 .
	 */
	public ArticleHeaderEntity findArticleHeaderById(String dbKey, String id, boolean latest) {
		var readPreference = latest ? ReadPreference.primary() : ReadPreference.secondaryPreferred();
		return this.mongoTemplateRouter.mongoTemplate(dbKey, readPreference, null)
			.findOne(Query.query(Criteria.where("_id").is(id)), ArticleHeaderEntity.class);
	}

	public Writer findArticleWriterById(String dbKey, String id) {
		var query = Query.query(Criteria.where("_id").is(id));
		query.fields().include("writer");
		var articleHeader = this.mongoTemplateRouter.mongoTemplate(dbKey).findOne(query, ArticleHeaderEntity.class);
		if (articleHeader == null) {
			return null;
		}
		return articleHeader.getWriter();
	}

	public List<ArticleHeaderEntity> listArticleHeader(String dbKey, ListArticleHeaderRequestParam param) {
		var criteria = new Criteria();
		if (StringUtils.hasText(param.boardId())) {
			criteria.and("boardId").is(param.boardId());
		}

		var sortOrder = SortOrder.fromValue(param.sort());
		if (StringUtils.hasText(param.prevArticleId())) {
			switch (sortOrder) {
				case TIME_DESC -> criteria.and("id").lt(param.prevArticleId());
				case TIME_ASC -> criteria.and("id").gt(param.prevArticleId());
			}
		}
		var sort = Sort.by(sortOrder.getDirection(), sortOrder.getField());
		var query = Query.query(criteria).with(sort).limit(param.size() + 1);
		return this.mongoTemplateRouter.mongoTemplate(dbKey).find(query, ArticleHeaderEntity.class);
	}

	public ArticleHeaderEntity insert(String dbKey, ArticleHeaderEntity header) {
		return this.insert(this.mongoTemplateRouter.mongoTemplate(dbKey), header);
	}

	public ArticleHeaderEntity insert(MongoTemplate template, ArticleHeaderEntity header) {
		this.validateHeader(header);
		header.setActionDateTime(new ActionDateTime());
		return template.insert(header);
	}

	private void validateHeader(ArticleHeaderEntity header) {
		AssertHelper.hasText(header.getBoardId(), "boardId is required");
		AssertHelper.hasText(header.getTitle(), "title is required");
		AssertHelper.notNull(header.getWriter(), "writer is required");
		AssertHelper.hasText(header.getWriter().getWid(), "writerId is required");
		AssertHelper.hasText(header.getWriter().getName(), "writerName is required");
	}

	public ArticleHeaderEntity modifyArticleHeader(String service, String id, String title) {
		AssertHelper.hasText(title, "title is required");

		var update = new Update();
		update.set("title", title);
		update.set("actionDateTime.updatedAt", ZonedDateTime.now());
		return this.mongoTemplateRouter.mongoTemplate(service, ReadPreference.primary(), WriteConcern.MAJORITY)
			.findAndModify(Query.query(Criteria.where("_id").is(id)), update,
					FindAndModifyOptions.options().returnNew(true), ArticleHeaderEntity.class);
	}

}
