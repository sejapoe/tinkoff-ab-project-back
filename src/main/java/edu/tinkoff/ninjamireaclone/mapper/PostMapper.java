package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.post.request.CreatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.request.UpdatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.model.Post;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "anonymous", source = "isAnonymous")
    Post toPost(CreatePostRequestDto requestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "anonymous", expression = "java(false)")
    Post toPost(CreateTopicRequestDto requestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "anonymous", source = "isAnonymous")
    Post toPost(UpdatePostRequestDto requestDto);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "isAuthor", expression = "java(post.getAuthor().getId() == userId)")
    @Mapping(target = "authorId", expression = "java(post.isAnonymous() ? -1L : post.getAuthor().getId())")
    @Mapping(target = "authorName", expression = "java(post.isAnonymous() ? \"Аноним\" : post.getAuthor().getDisplayName())")
    PostResponseDto toPostResponseDto(Post post, @Context Long userId);
}