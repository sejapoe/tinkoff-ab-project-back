package edu.tinkoff.ninjamireaclone.dto.news.response;

import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;

public record NewsCommentResponseDto(
        Long threadId,
        PostResponseDto post
) {
}
