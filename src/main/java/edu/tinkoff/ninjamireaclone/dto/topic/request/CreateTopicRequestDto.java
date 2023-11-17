package edu.tinkoff.ninjamireaclone.dto.topic.request;

import jakarta.validation.constraints.NotNull;

public record CreateTopicRequestDto(
        @NotNull Long parentId,
        @NotNull String name
) {
}
