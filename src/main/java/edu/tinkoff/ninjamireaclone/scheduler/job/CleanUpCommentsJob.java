package edu.tinkoff.ninjamireaclone.scheduler.job;

import edu.tinkoff.ninjamireaclone.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
@Slf4j
public class CleanUpCommentsJob extends QuartzJobBean {

    private final PostService postService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        var deletedComments = postService.cleanUpComments();
        log.info("Удалены комментарии: " + deletedComments);
    }
}
