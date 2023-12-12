package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.news.response.NewsCommentResponseDto;
import edu.tinkoff.ninjamireaclone.dto.news.response.NewsResponseDto;
import edu.tinkoff.ninjamireaclone.dto.news.response.ShortNewsResponseDto;
import edu.tinkoff.ninjamireaclone.model.PostEntity;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DocumentMapper.class, PostMapper.class})
public interface NewsMapper {

    @Mapping(target = "id", source = "news.id")
    @Mapping(target = "title", source = "news.name")
    @Mapping(target = "postId", source = "newsPost.id")
    @Mapping(target = "files", source = "newsPost.documents")
    @Mapping(target = "text", source = "newsPost.text")
    @Mapping(target = "createdAt", source = "newsPost.createdAt")
    NewsResponseDto toNewsResponseDto(SectionEntity news, PostEntity newsPost);

    @Mapping(target = "title", source = "news.name")
    ShortNewsResponseDto toShortNewsResponseDto(SectionEntity news);

    @Mapping(target = "threadId", source = "comment.id")
    @Mapping(target = "post", source = "post")
    NewsCommentResponseDto toNewsCommentResponseDto(TopicEntity comment, PostEntity post, @Context Long userId, @Context boolean isAdmin);
}
