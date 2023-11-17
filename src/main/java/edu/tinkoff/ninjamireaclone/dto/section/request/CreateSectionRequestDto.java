package edu.tinkoff.ninjamireaclone.dto.section.request;

public record CreateSectionRequestDto(
        Long parentId,
        String name
) {
}
