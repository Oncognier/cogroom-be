package oncog.cogroom.domain.daily.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.controller.docs.DailyControllerDocs;
import oncog.cogroom.domain.daily.dto.request.DailyAnswerRequestDTO;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.service.DailyService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/daily")
public class DailyController implements DailyControllerDocs {

    private final DailyService dailyService;

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<DailyQuestionResponseDTO>> getDailyQuestion() {

        DailyQuestionResponseDTO response = dailyService.getTodayDailyQuestion();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }

    @PostMapping("/answers")
    public ResponseEntity<ApiResponse<String>> createDailyAnswer(@RequestBody @Valid DailyAnswerRequestDTO request) {

        dailyService.createDailyAnswer(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }


}
