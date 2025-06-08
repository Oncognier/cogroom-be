package oncog.cogroom.domain.streak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.streak.docs.StreakControllerDocs;
import oncog.cogroom.domain.streak.dto.response.StreakCalenderResponseDTO;
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
    public ResponseEntity<ApiResponse<StreakCalenderResponseDTO>> getStreakCalender() {
        StreakCalenderResponseDTO response = streakService.getStreakDates();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }
}
