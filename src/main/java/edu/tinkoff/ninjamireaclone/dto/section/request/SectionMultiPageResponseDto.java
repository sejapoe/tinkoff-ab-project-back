package edu.tinkoff.ninjamireaclone.dto.section.request;

import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.ShortTopicResponseDto;

import java.util.List;

public record SectionMultiPageResponseDto(
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements,
        List<ShortSectionResponseDto> subsections,
        List<ShortTopicResponseDto> topics
) {
}
