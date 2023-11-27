package edu.tinkoff.ninjamireaclone.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponseDto(
        @JsonProperty("account_id")
        Long accountId,
        String token
) {
}
