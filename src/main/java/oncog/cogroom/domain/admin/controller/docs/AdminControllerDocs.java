package oncog.cogroom.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.dto.response.PageResponse;
import oncog.cogroom.domain.admin.exception.AdminErrorCode;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;

import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @ApiErrorCodeExamples(
            value = {},
            include = {}
    )
    @Operation(summary = "어드민 페이지 회원 조회", description = "회원 관리 페이지에서 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AdminResponse.MemberListDTO>>> getMemberList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "데일리 질문 목록 조회", description = "데일리 질문 목록을 조회합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, AdminErrorCode.class, ApiErrorCode.class},
            include = {"INVALID_LEVEL_ERROR", "INVALID_CATEGORY_ERROR", "INTERNAL_SERVER_ERROR", "TOKEN_INVALID_ERROR",
                    "TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR", "FORBIDDEN_ERROR", "PAGE_OUT_OF_RANGE_ERROR"})
    ResponseEntity<ApiResponse<PageResponse<AdminResponse.DailyQuestionsDTO>>> getDailyQuestions(
            @RequestParam(required = false) List<Integer> category,
            @RequestParam(required = false) List<String> level,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 4, sort = "id", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "데일리 질문 등록", description = "데일리 질문을 등록합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, AdminErrorCode.class, ApiErrorCode.class},
            include = {"QUESTION_LIST_EMPTY_ERROR", "LEVEL_EMPTY_ERROR", "INVALID_LEVEL_ERROR",
                    "CATEGORY_EMPTY_ERROR", "INVALID_CATEGORY_ERROR", "INTERNAL_SERVER_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR", "FORBIDDEN_ERROR"})
    ResponseEntity<ApiResponse<String>> createDailyQuestions(@RequestBody AdminRequest.DailyQuestionsDTO request);

    public ResponseEntity<ApiResponse<Void>> deleteMembers(AdminRequest.DeleteMembersDTO request);

    public ResponseEntity<ApiResponse<Void>> updateMemberRole(@PathVariable Long memberId,
                                                              @RequestParam MemberRole role);

}
