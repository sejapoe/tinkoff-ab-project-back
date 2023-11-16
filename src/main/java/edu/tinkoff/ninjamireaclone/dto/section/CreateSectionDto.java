package edu.tinkoff.ninjamireaclone.dto.section;

public record CreateSectionDto(
        Long parentId,
        String name
) {
}
