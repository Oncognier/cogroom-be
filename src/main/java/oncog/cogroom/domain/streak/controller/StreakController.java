package oncog.cogroom.domain.streak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.streak.controller.docs.StreakControllerDocs;
import oncog.cogroom.domain.streak.dto.response.StreakResponse;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/streaks")
public class StreakController implements StreakControllerDocs {

    private final StreakService streakService;

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<StreakResponse.CalendarDTO>> getStreakCalendarWithDailyStreak() {
        StreakResponse.CalendarDTO response = streakService.getStreakCalendar();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }

    @GetMapping("/daily-streak")
    public ResponseEntity<ApiResponse<StreakResponse.DailyStreakDTO>> getDailyStreak() {
        StreakResponse.DailyStreakDTO response = streakService.getDailyStreak();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }
}
