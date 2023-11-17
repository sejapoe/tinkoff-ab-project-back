package edu.tinkoff.ninjamireaclone.dto.attachment.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AttachmentResponseDto(
        @JsonProperty("post_id")
        Long postId,
        @JsonProperty("document_id")
        Long documentId
) {
}
