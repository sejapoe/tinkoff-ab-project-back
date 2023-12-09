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
    @Mapping(target = "isAnonymous", source = "isAnonymous")
    @Mapping(target = "isOpening", expression = "java(false)")
    Post toPost(CreatePostRequestDto requestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "isAnonymous", expression = "java(false)")
    @Mapping(target = "isOpening", expression = "java(true)")
    Post toPost(CreateTopicRequestDto requestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "isAnonymous", source = "isAnonymous")
    @Mapping(target = "isOpening", expression = "java(false)")
    Post toPost(UpdatePostRequestDto requestDto);

    @Mapping(target = "isAnonymous", source = "post.anonymous")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "isAuthor", expression = "java(post.getAuthor().getId() == userId)")
    @Mapping(target = "authorId", expression = "java(!isAdmin && post.isAnonymous() ? -1L : post.getAuthor().getId())")
    @Mapping(target = "authorName", expression = "java(!isAdmin && post.isAnonymous() ? \"Аноним\" : post.getAuthor().getDisplayName())")
    PostResponseDto toPostResponseDto(Post post, @Context Long userId, @Context boolean isAdmin);
}