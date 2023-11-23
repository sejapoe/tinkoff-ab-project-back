package edu.tinkoff.ninjamireaclone.dto.common;

import java.util.List;

public record PageResponseDto<T>(
        int number,
        int size,
        int totalPages,
        long totalElements,
        List<T> content
) {
}
