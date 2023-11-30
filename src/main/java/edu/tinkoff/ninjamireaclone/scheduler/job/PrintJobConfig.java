package edu.tinkoff.ninjamireaclone.scheduler.job;

import edu.tinkoff.ninjamireaclone.config.SchedulerProperties;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PrintJobConfig {
    private final SchedulerProperties schedulerProperties;

    @Bean
    public JobDetail printJobDetail() {
        return JobBuilder
                .newJob(PrintJob.class)
                .withIdentity("printJob", schedulerProperties.getPermanentJobsGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger printTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(printJobDetail())
                .withIdentity("printJobTrigger", schedulerProperties.getPermanentJobsGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerProperties.getPrintJobCron()))
                .build();
    }
}
