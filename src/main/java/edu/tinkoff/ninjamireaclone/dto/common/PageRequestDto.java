package edu.tinkoff.ninjamireaclone.dto.common;

public record PageRequestDto(
        Integer pageNumber,
        Integer forPage
) {
    public PageRequestDto {
        if (pageNumber == null) pageNumber = 0;
        if (forPage == null) forPage = 10;
    }
}
