package edu.tinkoff.ninjamireaclone.dto.news.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record NewsResponseDto(
        Long id,
        @JsonProperty("post_id")
        Long postId,
        String title,
        String text,
        List<DocumentResponseDto> files,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
