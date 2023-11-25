package edu.tinkoff.ninjamireaclone.dto.topic.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateTopicRequestDto(
        @NotNull Long parentId,
        @NotNull String name,
        @NotNull Long authorId,
        @Size(max = 200)
        @NotBlank
        String text,
        @Size(max = 5)
        List<MultipartFile> files
) {
}
