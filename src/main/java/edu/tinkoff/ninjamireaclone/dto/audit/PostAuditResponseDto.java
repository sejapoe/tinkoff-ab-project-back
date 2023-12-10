package edu.tinkoff.ninjamireaclone.dto.audit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PostAuditResponseDto(
        Long id,
        String text,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
}
