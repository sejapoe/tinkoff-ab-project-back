package edu.tinkoff.ninjamireaclone.dto.section.request;

import jakarta.validation.constraints.NotNull;

public record UpdateSectionRequestDto(
        @NotNull Long id,
        @NotNull String name
) {
}
