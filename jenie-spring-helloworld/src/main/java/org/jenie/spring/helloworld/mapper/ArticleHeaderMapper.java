package org.jenie.spring.helloworld.mapper;

import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.entity.article.ArticleHeaderEntity;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleHeaderMapper {

	ArticleHeaderMapper INSTANCE = Mappers.getMapper(ArticleHeaderMapper.class);

	ArticleHeader toDto(ArticleHeaderEntity entity);

	@Mapping(target = "board", source = "board")
	@Mapping(target = ".", source = "articleHeader")
	ArticleHeader toDto(ArticleHeaderEntity articleHeader, BoardEntity board);

	ArticleHeaderEntity toEntity(ArticleHeader dto);

}
