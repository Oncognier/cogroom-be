package oncog.cogroom.domain.streak.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.streak.dto.response.StreakCalendarResponseDTO;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;

@Tag(name = "Streak", description = "스트릭 관련 API")
public interface StreakControllerDocs {

    @Operation(summary = "물방울(스트릭) 기록 조회", description = "물방울 기록을 리스트로 반환합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<StreakCalendarResponseDTO>> getStreakCalendar();
}
