package edu.tinkoff.ninjamireaclone.dto.post.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
    long id,
    long parent_id,
    String text,
    @JsonProperty("author_id")
    long authorId,
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    @JsonProperty("document_names")
    List<String> documentNames
) {
}
