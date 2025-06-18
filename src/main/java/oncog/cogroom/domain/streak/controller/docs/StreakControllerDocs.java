package oncog.cogroom.domain.streak.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import static oncog.cogroom.domain.streak.dto.StreakResponseDTO.*;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;

@Tag(name = "Streak", description = "스트릭 관련 API")
public interface StreakControllerDocs {

    @Operation(summary = "물방울(스트릭) 기록 조회", description = "물방울 기록을 리스트로 반환합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<StreakCalendarDTO>> getStreakCalendarWithDailyStreak();

    @Operation(summary = "데일리 스트릭 연속 일수 조회", description = "데일리 스트릭 연속 일수를 반환합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<DailyStreakDTO>> getDailyStreak();
}
