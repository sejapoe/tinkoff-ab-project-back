package edu.tinkoff.ninjamireaclone.utils.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OffsetPageable implements Pageable {
    private final int pageNumber;
    private final int pageSize;
    private final long offset;
    private final Sort sort;

    public static OffsetPageable of(int pageNumber, int pageSize) {
        return of(pageNumber, pageSize, 0L);
    }

    public static OffsetPageable of(int pageNumber, int pageSize, long offset) {
        return of(pageNumber, pageSize, offset, Sort.unsorted());
    }

    public static OffsetPageable of(int pageNumber, int pageSize, long offset, Sort sort) {
        return new OffsetPageable(pageNumber, pageSize, offset, sort);
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset + (long) pageSize * (long) pageNumber;
    }

    @Override
    public @NotNull Sort getSort() {
        return sort;
    }

    @Override
    public @NotNull OffsetPageable next() {
        return new OffsetPageable(pageNumber + 1, pageSize, offset, sort);
    }

    @Override
    public @NotNull OffsetPageable previousOrFirst() {
        return this.getPageNumber() == 0 ? this : new OffsetPageable(pageNumber - 1, pageSize, offset, sort);
    }

    @Override
    public @NotNull OffsetPageable first() {
        return new OffsetPageable(0, pageSize, offset, sort);
    }

    @Override
    public @NotNull OffsetPageable withPage(int pageNumber) {
        return new OffsetPageable(pageNumber, pageSize, offset, sort);
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }
}
