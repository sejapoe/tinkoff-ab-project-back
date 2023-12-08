package edu.tinkoff.ninjamireaclone.dto.settings.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TriggerResponseDto(
        @JsonProperty("trigger_key")
        String triggerKey,
        @JsonProperty("job_key")
        String jobKey,
        String cron

) {
}
