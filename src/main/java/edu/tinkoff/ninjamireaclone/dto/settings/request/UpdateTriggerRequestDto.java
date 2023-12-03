package edu.tinkoff.ninjamireaclone.dto.settings.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateTriggerRequestDto(
        @JsonProperty("trigger_key")
        @NotNull
        String triggerKey,
        @NotNull
        String group,
        @NotNull
        String cron
) {
}
