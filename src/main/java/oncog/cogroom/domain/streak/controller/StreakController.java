package oncog.cogroom.domain.streak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.streak.dto.response.StreakCalenderResponse;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/streaks")
public class StreakController {

    private final StreakService streakService;

    @GetMapping("/calender")
    public ResponseEntity<ApiResponse<StreakCalenderResponse>> getStreakCalender() {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        StreakCalenderResponse response = streakService.getStreakDates(user.getMemberId());

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }
}
