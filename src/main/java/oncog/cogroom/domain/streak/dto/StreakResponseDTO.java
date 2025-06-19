package oncog.cogroom.domain.streak.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StreakResponseDTO {

    @Getter
    @Builder
    public static class StreakCalendarDTO {
        private Integer dailyStreak;
        private List<String> streakDateList;
    }

    @Getter
    @Builder
    public static class DailyStreakDTO {
        private Integer dailyStreak;
    }
}
