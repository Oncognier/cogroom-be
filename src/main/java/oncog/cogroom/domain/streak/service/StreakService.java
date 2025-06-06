package oncog.cogroom.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.entity.StreakLog;
import oncog.cogroom.domain.streak.repository.StreakLogRepository;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import oncog.cogroom.domain.streak.dto.response.StreakCalenderResponseDTO;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class StreakService extends BaseService {

    private final StreakRepository streakRepository;
    private final StreakLogRepository streakLogRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public void updateAllMemberStreaks() {

        LocalDateTime startOfYesterday = getStartOfYesterday();
        LocalDateTime endOfYesterday = getEndOfYesterday();

        List<Streak> streaks = streakRepository.findAll();

        streaks.forEach(streak -> { // 추후 배치 적용 필요
            Long memberId = streak.getMember().getId();

           boolean hasYesterdayLog = hasLogForYesterday(memberId, startOfYesterday, endOfYesterday);

           // 해당 멤버가 전날에 작성한 log 정보가 없는 경우 누적 스트릭 일수를 0으로 초기화
           if (!hasYesterdayLog && streak.getTotalDays() > 0) { // 기존에 0이 아닐 때만 0으로 초기화 (불필요한 업데이트 방지)
               streak.resetTotalDays();
           }
        });
    }

    public StreakCalenderResponseDTO getStreakDates() {
        Long memberId = getMemberId();

        LocalDateTime startOfMonth = getStartOfCalenderMonth();
        LocalDateTime endOfMonth = getEndOfMonth();

        List<String> streakDates = streakLogRepository
                .findAllByMemberIdAndCreatedAtBetween(memberId, startOfMonth, endOfMonth).stream()
                .map(log -> log.getCreatedAt().toLocalDate().format(formatter))
                .distinct()
                .sorted()
                .toList();

        return StreakCalenderResponseDTO.builder()
                .streakDateList(streakDates)
                .build();
    }

    private boolean hasLogForYesterday(Long memberId, LocalDateTime start, LocalDateTime end) {
        return streakLogRepository.existsByMemberIdAndCreatedAtBetween(memberId, start, end);
    }

    private LocalDateTime getStartOfYesterday() {
        return LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
    }

    private LocalDateTime getEndOfYesterday() {
        return getStartOfYesterday().plusDays(1).minusNanos(1);
    }

    private LocalDateTime getStartOfCalenderMonth() {
        LocalDate firstDayOfMonth =  LocalDate.now().withDayOfMonth(1);
        DayOfWeek startDayOfWeek = firstDayOfMonth.getDayOfWeek();

        // 이번 달 시작 일이 월요일이 아닐 경우, 그 주의 월요일로 변경
        if (startDayOfWeek != DayOfWeek.MONDAY) {
            int tmpSubtract = (startDayOfWeek.getValue() + 6) % 7;
            firstDayOfMonth = firstDayOfMonth.minusDays(tmpSubtract);
        }

        return firstDayOfMonth.atStartOfDay();
    }

    private LocalDateTime getEndOfMonth() {
        return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);
    }

    public Streak getOrCreateStreak(Member member) {
        return streakRepository.findByMemberId(member.getId())
                .orElseGet(() -> streakRepository.save(
                        Streak.builder().member(member).build()
                ));
    }

    public void createStreakLog(Member member, Streak streak) {
        streakLogRepository.save(StreakLog.builder().member(member).streak(streak).build());
    }

    public int getStreakDays(Long memberId) {
        return streakRepository.findByMemberId(memberId)
                .map(Streak::getTotalDays)
                .orElse(0);
    }

}
