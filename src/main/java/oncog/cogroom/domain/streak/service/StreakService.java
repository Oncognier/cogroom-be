package oncog.cogroom.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.repository.StreakLogRepository;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class StreakService {

    private final StreakRepository streakRepository;
    private final StreakLogRepository streakLogRepository;

    // 특정 멤버의 스트릭 기록 초기화
    @Transactional
    public void updateAllMemberStreaks() {
        List<Streak> streaks = streakRepository.findAll();

        LocalDateTime startOfYesterday = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfYesterday = startOfYesterday.plusDays(1).minusNanos(1);

        System.out.println("startOfYesterday: " + startOfYesterday);
        System.out.println("endOfYesterday: " + endOfYesterday);

        streaks.forEach(streak -> { // 추후 배치 적용 필요
            Long memberId = streak.getMember().getId();

           boolean hasYesterdayLog = streakLogRepository.existsByMemberIdAndCreatedAtBetween(
                   memberId, startOfYesterday, endOfYesterday
           );

           // 해당 멤버가 전날에 작성한 log 정보가 없는 경우 누적 스트릭 일수를 0으로 초기화
           if (!hasYesterdayLog && streak.getTotalDays() > 0) { // 기존에 0이 아닐 때만 0으로 초기화 (불필요한 업데이트 방지)
               streak.resetTotalDays();
           }
        });
    }
}
