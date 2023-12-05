package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.common.PageRequestDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageMapper {
    public <T> PageResponseDto<T> toResponseDto(Page<T> page) {
        return new PageResponseDto<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.stream().toList()
        );
    }

    public <T, D> PageResponseDto<D> toResponseDto(Page<T> page, Function<T, D> elementMapper) {
        return toResponseDto(page.map(elementMapper));
    }

    public Pageable fromRequestDto(PageRequestDto dto) {
        return PageRequest.of(dto.pageNumber(), dto.forPage(), Sort.by("id"));
    }
}
