package oncog.cogroom.domain.daily.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobExecutionTimerLogger implements JobExecutionListener {

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
    }
}
