package edu.tinkoff.ninjamireaclone.dto.section.response;

import java.util.List;

public record SectionResponseDto(
        Long id,
        String name,
        List<ShortSectionResponseDto> subsections,
        ShortSectionResponseDto parent
) {
}
