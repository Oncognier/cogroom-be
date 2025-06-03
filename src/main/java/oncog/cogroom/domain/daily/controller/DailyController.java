package oncog.cogroom.domain.daily.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponse;
import oncog.cogroom.domain.daily.service.DailyService;
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
@RequestMapping("/api/v1/daily")
public class DailyController {

    private final DailyService dailyService;

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<DailyQuestionResponse>> getDailyQuestion() {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DailyQuestionResponse response = dailyService.getTodayDailyQuestion(user.getMemberId());

        return ResponseEntity
                .status(ApiSuccessCode.SUCCESS.getStatus())
                .body(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }


}
