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

    Post toPost(CreatePostRequestDto requestDto);

    Post toPost(UpdatePostRequestDto requestDto);

    @Mapping(target = "documentNames", source = "documents", qualifiedByName = "documentNames")
    PostResponseDto toPostResponseDto(Post post);

    @Named("documentNames")
    default List<String> documentsToDocumentNames(Set<Document> documents) {
        return documents.stream().map(Document::getFilename).collect(Collectors.toList());
    }
}
