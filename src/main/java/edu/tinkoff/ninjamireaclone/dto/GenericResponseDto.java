package edu.tinkoff.ninjamireaclone.dto;

public record GenericResponseDto<T>(
        int statusCode,
        T message
) {
}
