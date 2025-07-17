package oncog.cogroom.domain.daily.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyQuestionAssignBatchSchduler {

    private final JobLauncher jobLauncher;
    private final Job dailyQuestionAssignJob;

    @Scheduled(cron = "0 0 0 * * *") // 자정마다
//    @Scheduled(cron = "0 */2 * * * *") // 1분마다, 테스트용
    public void assignDailyQuestionsAtMidnight() {
        log.info("데일리 질문 할당 시작");
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("datetime", LocalDateTime.now().toString())  // 실행 시간을 파라미터로 추가
                    .toJobParameters();

            // 배치 Job 실행
            jobLauncher.run(dailyQuestionAssignJob, jobParameters);
            log.info("데일리 질문 할당 배치 완료");

        } catch (JobExecutionAlreadyRunningException e) {
            // 동일한 Job이 이미 실행 중인 경우
            log.error("배치 작업이 이미 실행 중입니다.", e);
        } catch (JobRestartException e) {
            // 재시작 불가능한 Job을 재시작하려는 경우
            log.error("배치 작업 재시작 중 오류가 발생했습니다.", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            // 이미 완료된 JobInstance를 다시 실행하려는 경우
            log.error("배치 작업이 이미 완료되었습니다.", e);
        } catch (JobParametersInvalidException e) {
            // JobParameters가 유효하지 않은 경우
            log.error("배치 작업 파라미터가 유효하지 않습니다.", e);
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("배치 작업 실행 중 예상치 못한 오류가 발생했습니다.", e);
        }
        log.info("데일리 질문 할당 완료");
    }
}
