package edu.tinkoff.ninjamireaclone.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final Scheduler scheduler;

    /**
     * Update job trigger
     * @param key key of the trigger
     * @param group group of the trigger
     * @param cron new cron expression
     * @return updated trigger
     */
    @SneakyThrows
    public CronTrigger updateTrigger(String key, String group, String cron) {
        var triggerKey = new TriggerKey(key, group);
        var trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        trigger.setCronExpression(cron);
        scheduler.rescheduleJob(triggerKey, trigger);
        return trigger;
    }
}
