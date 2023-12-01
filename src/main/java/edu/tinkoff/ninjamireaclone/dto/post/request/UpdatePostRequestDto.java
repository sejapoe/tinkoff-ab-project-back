package edu.tinkoff.ninjamireaclone.dto.post.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePostRequestDto(
        @NotNull Long id,
        @JsonProperty("parent_id")
        @NotNull Long parentId,
        @JsonProperty("author_id")
        @NotNull Long authorId,

        @Size(max = 200)
        @NotBlank
        String text,

        @JsonProperty("is_anonymous")
        boolean isAnonymous
) {
}
