package edu.tinkoff.ninjamireaclone.dto.auth.response;

public record SignUpResponseDto(
        Long id,
        String name,

        String token
) {
}
