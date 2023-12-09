package edu.tinkoff.ninjamireaclone.dto.post.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
        long id,
        @JsonProperty("parent_id")
        long parentId,
        String text,
        @JsonProperty("is_author")
        boolean isAuthor,

        @JsonProperty("is_anonymous")
        boolean isAnonymous,
        
        @JsonProperty("author_id")
        long authorId,
        @JsonProperty("author_name")
        String authorName,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt,
        List<DocumentResponseDto> documents
) {
}
