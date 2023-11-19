package edu.tinkoff.ninjamireaclone.dto.topic.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;

import java.util.List;

public record TopicResponseDto(
        long id,
        String name,
        @JsonProperty("parent_id")
        Long parentId,
        List<PostResponseDto> posts
) {
}
