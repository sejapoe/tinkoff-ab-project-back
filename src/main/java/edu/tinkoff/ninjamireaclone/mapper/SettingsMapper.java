package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.settings.response.TriggerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.quartz.CronTrigger;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    @Mapping(target = "jobKey", expression = "java(trigger.getJobKey().toString())")
    @Mapping(target = "triggerKey", expression = "java(trigger.getKey().toString())")
    @Mapping(target = "cron", source = "cronExpression")
    TriggerResponseDto toTriggerResponseDto(CronTrigger trigger);
}
