package org.jenie.spring.test.helloworld.mapper;

import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.test.helloworld.entity.article.ArticleHeaderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleHeaderMapper {

	ArticleHeaderMapper INSTANCE = Mappers.getMapper(ArticleHeaderMapper.class);

	ArticleHeader toDto(ArticleHeaderEntity entity);

	ArticleHeaderEntity toEntity(ArticleHeader dto);

}
