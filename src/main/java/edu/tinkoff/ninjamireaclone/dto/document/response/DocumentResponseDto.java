package edu.tinkoff.ninjamireaclone.dto.document.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.ninjamireaclone.model.DocumentType;

public record DocumentResponseDto(
        long id,
        String filename,
        @JsonProperty("original_name")
        String originalName,
        DocumentType type
) {
}