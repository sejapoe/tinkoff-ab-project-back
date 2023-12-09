package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.settings.request.UpdateTriggerRequestDto;
import edu.tinkoff.ninjamireaclone.dto.settings.response.TriggerResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.SettingsMapper;
import edu.tinkoff.ninjamireaclone.service.SettingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "settings", description = "Настройки приложения")
@Slf4j
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    @PutMapping("/job")
    @PreAuthorize("hasAuthority('MANAGE_JOBS')")
    public ResponseEntity<TriggerResponseDto> updateTrigger(@RequestBody @Valid UpdateTriggerRequestDto requestDto)
    {
        var trigger = settingsService.updateTrigger(requestDto.triggerKey(), requestDto.group(), requestDto.cron());
        log.info("Изменены настройки триггера: " + trigger.getKey());
        return ResponseEntity.ok(settingsMapper.toTriggerResponseDto(trigger));
    }
}
