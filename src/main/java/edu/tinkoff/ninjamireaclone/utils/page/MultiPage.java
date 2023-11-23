package edu.tinkoff.ninjamireaclone.utils.page;

import java.util.List;

public record MultiPage<A1, A2>(
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements,
        List<A1> content1,
        List<A2> content2
) {
}
