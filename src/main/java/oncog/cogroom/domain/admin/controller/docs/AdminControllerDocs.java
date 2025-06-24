package oncog.cogroom.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.global.common.response.PageResponse;
import oncog.cogroom.domain.admin.exception.AdminErrorCode;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;

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
            include = {"LEVEL_INVALID_ERROR", "CATEGORY_INVALID_ERROR", "INTERNAL_SERVER_ERROR", "TOKEN_INVALID_ERROR",
                    "TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR", "FORBIDDEN_ERROR", "PAGE_OUT_OF_RANGE_ERROR"})
    ResponseEntity<ApiResponse<PageResponse<AdminResponse.DailyQuestionsDTO>>> getDailyQuestions(
            @RequestParam(required = false) List<Integer> category,
            @RequestParam(required = false) List<String> level,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 4, sort = "id", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "데일리 질문 등록", description = "데일리 질문을 등록합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, AdminErrorCode.class, ApiErrorCode.class},
            include = {"QUESTION_LIST_EMPTY_ERROR", "LEVEL_EMPTY_ERROR", "LEVEL_INVALID_ERROR",
                    "CATEGORY_EMPTY_ERROR", "CATEGORY_INVALID_ERROR", "INTERNAL_SERVER_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR", "FORBIDDEN_ERROR"})
    ResponseEntity<ApiResponse<String>> createDailyQuestions(@RequestBody AdminRequest.DailyQuestionsDTO request);


    @Operation(summary = "사용자 삭제", description = "회원 관리 페이지에서 회원을 삭제합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class,ApiErrorCode.class},
            include = { "MEMBER_NOT_FOUND_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR",
                    "INTERNAL_SERVER_ERROR","FORBIDDEN_ERROR"}
    )
    public ResponseEntity<ApiResponse<Void>> deleteMembers(AdminRequest.DeleteMembersDTO request);

    @Operation(summary = "사용자 권한 변경" ,description = "사용자의 권한을 변경합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class,ApiErrorCode.class},
            include = { "MEMBER_NOT_FOUND_ERROR",
                    "TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR",
                    "INTERNAL_SERVER_ERROR","FORBIDDEN_ERROR"}
    )
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(@PathVariable Long memberId,
                                                              @RequestParam MemberRole role);


    @Operation(summary = "사용자 데일리 콘텐츠 조회", description = "회원 관리 페이지에서 사용자의 데일리 콘텐츠 내역을 조회합니다.")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, AdminErrorCode.class, ApiErrorCode.class},
            include = { "INTERNAL_SERVER_ERROR", "TOKEN_INVALID_ERROR",
                    "TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR", "FORBIDDEN_ERROR",
                    "PAGE_OUT_OF_RANGE_ERROR"}
    )
    public ResponseEntity<ApiResponse<PageResponse<AdminResponse.MemberDailyListDTO>>> getDailyContents(
            @PathVariable Long memberId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) QuestionLevel questionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate,
            @PageableDefault(size = 10, sort = "answeredAt", direction = Sort.Direction.DESC) Pageable pageable);

}
