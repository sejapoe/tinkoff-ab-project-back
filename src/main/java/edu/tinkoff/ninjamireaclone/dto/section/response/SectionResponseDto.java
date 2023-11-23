package edu.tinkoff.ninjamireaclone.dto.section.response;

import edu.tinkoff.ninjamireaclone.dto.section.request.SectionMultiPageResponseDto;

public record SectionResponseDto(
        Long id,
        String name,
        ShortSectionResponseDto parent,
        SectionMultiPageResponseDto page
) {
}
