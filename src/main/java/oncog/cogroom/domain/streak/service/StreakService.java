package oncog.cogroom.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.entity.StreakLog;
import oncog.cogroom.domain.streak.repository.StreakLogRepository;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import oncog.cogroom.domain.streak.dto.response.StreakCalendarResponseDTO;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;

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

    public StreakCalendarResponseDTO getStreakDates() {
        Member member = getMember();

        LocalDateTime startOfMonth = getStartOfCalendarMonth();
        LocalDateTime endOfMonth = getEndOfMonth();

        List<String> streakDates = streakLogRepository
                .findAllByMemberAndCreatedAtBetween(member, startOfMonth, endOfMonth).stream()
                .map(log -> log.getCreatedAt().toLocalDate().format(formatter))
                .distinct()
                .sorted()
                .toList();

        int streakDays = getStreakDays(member);

        return StreakCalendarResponseDTO.builder()
                .streakDays(streakDays)
                .streakDateList(streakDates)
                .build();
    }

    private LocalDateTime getStartOfCalendarMonth() {
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
        return streakRepository.findByMember(member)
                .orElseGet(() -> streakRepository.save(
                        Streak.builder().member(member).build()
                ));
    }

    public void createStreakLog(Member member, Streak streak) {
        streakLogRepository.save(StreakLog.builder().member(member).streak(streak).build());
    }

    // 스트릭 연속 일자 조회 (totalDays)
    public int getStreakDays(Member member) {
        return streakRepository.findByMember(member)
                .map(Streak::getTotalDays)
                .orElse(0);
    }

}
