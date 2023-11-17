package edu.tinkoff.ninjamireaclone.dto.topic.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopicResponseDto(
    long id,
    @JsonProperty("parent_id")
    long parentId,
    String name
) {
}
