package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.annotation.IsAdmin;
import edu.tinkoff.ninjamireaclone.dto.settings.request.UpdateTriggerRequestDto;
import edu.tinkoff.ninjamireaclone.dto.settings.response.TriggerResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.SettingsMapper;
import edu.tinkoff.ninjamireaclone.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    @PutMapping("/job")
    @IsAdmin
    public ResponseEntity<TriggerResponseDto> updateTrigger(@RequestBody @Valid UpdateTriggerRequestDto requestDto)
    {
        System.out.println(requestDto);
        var trigger = settingsService.updateTrigger(requestDto.triggerKey(), requestDto.group(), requestDto.cron());
        return ResponseEntity.ok(settingsMapper.toTriggerResponseDto(trigger));
    }
}
