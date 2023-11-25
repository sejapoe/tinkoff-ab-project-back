package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.post.request.CreatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.request.UpdatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    Post toPost(CreatePostRequestDto requestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    Post toPost(UpdatePostRequestDto requestDto);

    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    PostResponseDto toPostResponseDto(Post post);
}
