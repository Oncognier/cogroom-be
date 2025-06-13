package oncog.cogroom.domain.streak.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.streak.service.StreakUpdateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakSchduler {

    private final StreakUpdateService streakUpdateService;

    @Scheduled(cron = "0 0 0 * * *") // 자정마다
//    @Scheduled(cron = "0 */2 * * * *") // 2분마다, 테스트용
    public void updateStreakAtMidnight() {
        log.info("스트릭 기록 초기화 시작");
        streakUpdateService.updateAllMemberStreaks();
        log.info("스트릭 기록 초기화 완료");
    }
}
