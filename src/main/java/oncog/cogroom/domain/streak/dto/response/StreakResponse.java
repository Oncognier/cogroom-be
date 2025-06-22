package oncog.cogroom.domain.streak.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StreakResponse {

    @Getter
    @Builder
    public static class CalendarDTO {
        private List<String> streakDateList;
    }

    @Getter
    @Builder
    public static class DailyStreakDTO {
        private Integer dailyStreak;
    }
}
