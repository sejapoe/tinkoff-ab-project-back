package edu.tinkoff.ninjamireaclone.dto.section.response;

import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;

import java.util.List;

public record SectionResponseDto(
        Long id,
        String name,
        List<ShortSectionResponseDto> subsections,
        List<TopicResponseDto> topics,
        ShortSectionResponseDto parent
) {
}
