package edu.tinkoff.ninjamireaclone.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record JwtResponseDto(
        @JsonProperty("account_id")
        Long accountId,
        String username,
        String token,
        List<String> roles
) {
}
