package edu.tinkoff.ninjamireaclone.dto.section;

import java.util.List;

public record SectionDto(
        Long id,
        String name,
        List<ShortSectionDto> subsections,
        ShortSectionDto parent
) {
}
