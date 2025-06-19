package oncog.cogroom.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.repository.StreakLogRepository;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class StreakUpdateService extends BaseService {

    private final StreakRepository streakRepository;
    private final StreakLogRepository streakLogRepository;

    //
    @Transactional
    public void updateAllMemberStreaks() {

        LocalDateTime startOfYesterday = getStartOfYesterday();
        LocalDateTime endOfYesterday = getEndOfYesterday();

        List<Streak> streaks = streakRepository.findAll();

        streaks.forEach(streak -> { // 추후 배치 적용 필요
            Member member = streak.getMember();

           boolean hasYesterdayLog = hasLogForYesterday(member, startOfYesterday, endOfYesterday);

           // 해당 멤버가 전날에 작성한 log 정보가 없는 경우 누적 스트릭 일수를 0으로 초기화
           if (!hasYesterdayLog && streak.getDailyStreak() > 0) { // 기존에 0이 아닐 때만 0으로 초기화 (불필요한 업데이트 방지)
               streak.resetTotalDays();
           }
        });
    }

    private boolean hasLogForYesterday(Member member, LocalDateTime start, LocalDateTime end) {
        return streakLogRepository.existsByMemberAndCreatedAtBetween(member, start, end);
    }

    private LocalDateTime getStartOfYesterday() {
        return LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
    }

    private LocalDateTime getEndOfYesterday() {
        return getStartOfYesterday().plusDays(1).minusNanos(1);
    }

}
