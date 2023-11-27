package edu.tinkoff.ninjamireaclone.dto.auth.request;

public record JwtRequestDto(
        String name,
        String password
) {
}
