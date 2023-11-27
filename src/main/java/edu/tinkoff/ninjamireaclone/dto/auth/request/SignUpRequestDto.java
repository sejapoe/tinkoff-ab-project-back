package edu.tinkoff.ninjamireaclone.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpRequestDto(
        String name,
        String password,
        @JsonProperty("confirm_password")
        String confirmPassword
) {
}
