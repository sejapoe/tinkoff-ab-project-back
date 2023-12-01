package edu.tinkoff.ninjamireaclone.scheduler.job;

import edu.tinkoff.ninjamireaclone.config.SchedulerProperties;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CleanUpCommentsJobConfig {
    private final SchedulerProperties schedulerProperties;

    @Bean
    public JobDetail cleanUpCommentsJobDetail() {
        return JobBuilder
                .newJob(CleanUpCommentsJob.class)
                .withIdentity("cleanUpCommentsJob", schedulerProperties.getPermanentJobsGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger cleanUpCommentsJobTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(cleanUpCommentsJobDetail())
                .withIdentity("cleanUpCommentsJobTrigger", schedulerProperties.getPermanentJobsGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerProperties.getCleanUpCommentsJobCron()))
                .build();
    }
}
