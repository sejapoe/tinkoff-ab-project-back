package edu.tinkoff.ninjamireaclone.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePostRequestDto(
        @NotNull Long id,

        @Size(max = 200)
        @NotBlank
        String text
) {
}
