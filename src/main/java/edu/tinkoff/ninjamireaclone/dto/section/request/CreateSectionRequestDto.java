package edu.tinkoff.ninjamireaclone.dto.section.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CreateSectionRequestDto(
        @JsonProperty("parent_id")
        @NotNull Long parentId,
        @NotNull String name
) {
}
