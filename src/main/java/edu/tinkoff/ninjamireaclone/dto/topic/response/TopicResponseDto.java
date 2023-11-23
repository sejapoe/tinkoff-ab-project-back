package edu.tinkoff.ninjamireaclone.dto.topic.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;

public record TopicResponseDto(
        long id,
        String name,
        @JsonProperty("parent_id")
        Long parentId,
        PageResponseDto<PostResponseDto> posts
) {
}
