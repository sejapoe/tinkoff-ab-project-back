package edu.tinkoff.ninjamireaclone.dto.news.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentInThreadRequestDto(
        @NotNull Long threadId,
        @Size(max = 200)
        @NotBlank
        String text,
        boolean isAnonymous
) {
}