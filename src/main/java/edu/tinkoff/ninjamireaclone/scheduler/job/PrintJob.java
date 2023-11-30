package edu.tinkoff.ninjamireaclone.scheduler.job;

import edu.tinkoff.ninjamireaclone.service.PrintService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class PrintJob extends QuartzJobBean {
    private final PrintService printService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        printService.print();
    }
}
