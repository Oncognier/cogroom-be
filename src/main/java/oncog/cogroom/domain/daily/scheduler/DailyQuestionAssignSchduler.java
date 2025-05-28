package oncog.cogroom.domain.daily.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.service.DailyQuestionAssignService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyQuestionAssignSchduler {

    private final DailyQuestionAssignService dailyQuestionAssignService;

    @Scheduled(cron = "0 0 0 * * *") // 자정마다
//    @Scheduled(cron = "0 */1 * * * *") // 1분마다, 테스트용
    public void assignDailyQuestionsAtMidnight() {
        log.info("Daily question assignment started.");
        dailyQuestionAssignService.assignDailyQuestions();
        log.info("Daily question assignment completed.");
    }
}
