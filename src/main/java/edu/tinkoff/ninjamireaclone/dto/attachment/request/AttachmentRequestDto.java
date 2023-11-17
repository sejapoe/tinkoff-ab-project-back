package edu.tinkoff.ninjamireaclone.dto.attachment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AttachmentRequestDto(

        @JsonProperty("post_id")
        @NotNull Long postId,
        @JsonProperty("document_id")
        @NotNull Long documentId
) {
}
