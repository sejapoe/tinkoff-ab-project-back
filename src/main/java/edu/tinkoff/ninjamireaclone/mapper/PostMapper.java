package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.post.request.CreatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.request.UpdatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mapping(target = "documentNames", source = "documents", qualifiedByName = "documentNames")
    PostResponseDto toPostResponseDto(Post post);

    @Named("documentNames")
    default List<String> documentsToDocumentNames(Set<Document> documents) {
        return documents.stream().map(Document::getFilename).collect(Collectors.toList());
    }
}
