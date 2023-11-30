package edu.tinkoff.ninjamireaclone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerProperties {
    private String permanentJobsGroupName;
    private String printJobCron;
}
