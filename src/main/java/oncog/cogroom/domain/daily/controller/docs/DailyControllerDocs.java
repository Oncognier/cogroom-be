package oncog.cogroom.domain.daily.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.daily.dto.request.DailyAnswerRequestDTO;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.dto.response.HasAnsweredResponseDTO;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Daily", description = "데일리 관련 API")
public interface DailyControllerDocs {

    @Operation(summary = "데일리 질문 조회", description = "멤버가 할당받은 데일리 질문을 조회합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, DailyErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "QUESTION_NOT_FOUND_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<DailyQuestionResponseDTO>> getDailyQuestion();

    @Operation(summary = "데일리 답변 등록", description = "데일리 질문에 대한 답변을 등록합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, DailyErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "QUESTION_NOT_FOUND_ERROR", "ANSWER_ALREADY_EXIST_ERROR", "EMPTY_FILED_ERROR",
                    "ASSIGNED_QUESTION_NOT_FOUND_ERROR", "ANSWER_LENGTH_EXCEEDED_ERROR", "ANSWER_TIME_EXPIRED_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<String>> createDailyAnswer(@RequestBody @Valid DailyAnswerRequestDTO request);

    @Operation(summary = "데일리 답변 수정", description = "데일리 질문에 대한 답변을 수정합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, DailyErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "QUESTION_NOT_FOUND_ERROR", "ANSWER_ALREADY_EXIST_ERROR", "EMPTY_FILED_ERROR",
                    "ASSIGNED_QUESTION_NOT_FOUND_ERROR", "ANSWER_LENGTH_EXCEEDED_ERROR", "ANSWER_TIME_EXPIRED_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<String>> updateDailyAnswer(@RequestBody @Valid DailyAnswerRequestDTO request);

    @Operation(summary = "데일리 이전 답변 존재 여부 조회", description = "멤버가 이전에 답변한 이력이 있는지 조회합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, DailyErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"MEMBER_NOT_FOUND_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_EXPIRED_ERROR", "INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<HasAnsweredResponseDTO>> getHasAnswered();
}
