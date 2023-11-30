package edu.tinkoff.ninjamireaclone.scheduler;

import edu.tinkoff.ninjamireaclone.config.SchedulerProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {
    private final SchedulerProperties schedulerProperties;

    @Bean
    @SneakyThrows
    public Scheduler scheduler(List<Trigger> triggers, List<JobDetail> jobDetails, SchedulerFactoryBean factory) {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        var scheduler = factory.getScheduler();
        revalidateJobs(jobDetails, scheduler);
        rescheduleTriggers(triggers, scheduler);
        scheduler.start();
        return scheduler;
    }

    @SneakyThrows
    public void revalidateJobs(List<JobDetail> jobDetails, Scheduler scheduler) {
        var jobKeysInCode = jobDetails.stream().map(JobDetail::getKey).collect(Collectors.toSet());
        Set<JobKey> jobKeysInDatabase = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(schedulerProperties.getPermanentJobsGroupName()));
        for (JobKey jobKey : jobKeysInDatabase) {
            if (!jobKeysInCode.contains(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }

    @SneakyThrows
    public void rescheduleTriggers(List<Trigger> triggers, Scheduler scheduler) {
        for (Trigger trigger : triggers) {
            if (!scheduler.checkExists(trigger.getKey())) {
                scheduler.scheduleJob(trigger);
            } else {
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            }
        }
    }
}
