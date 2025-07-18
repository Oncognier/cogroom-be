package oncog.cogroom.global.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchJobLogger implements JobExecutionListener {

    private long startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("[배치 시작] Job: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("[배치 종료] Job: {}, 상태: {}, 실행 시간: {}ms",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                duration);

        log.info("[실행 파라미터] {}", jobExecution.getJobParameters());

        jobExecution.getStepExecutions().forEach(step -> {
            log.info("[스텝 정보] Step: {}, 상태: {}, 읽은 건수: {}, 쓴 건수: {}, 실패 건수: {}",
                    step.getStepName(),
                    step.getStatus(),
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getWriteSkipCount()
            );
        });

        if (jobExecution.getAllFailureExceptions() != null && !jobExecution.getAllFailureExceptions().isEmpty()) {
            log.error("[실패 예외 목록]");
            jobExecution.getAllFailureExceptions().forEach(e -> log.error("예외: ", e));
        }
    }

}
