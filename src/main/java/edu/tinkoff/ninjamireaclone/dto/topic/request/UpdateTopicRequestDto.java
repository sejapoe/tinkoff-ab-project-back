package edu.tinkoff.ninjamireaclone.dto.topic.request;

import jakarta.validation.constraints.NotNull;

public record UpdateTopicRequestDto(
        @NotNull Long id,
        @NotNull Long parentId,
        @NotNull String name
) {
}
