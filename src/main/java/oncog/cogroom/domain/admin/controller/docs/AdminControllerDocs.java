package oncog.cogroom.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.dto.response.PageResponse;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Admin", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "회원 목록 조회", description = "회원 목록을 조회합니다.")
    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class},
            include = {"INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<PageResponse<AdminResponse.MemberListDTO>>> getMemberList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "데일리 질문 추가", description = "데일리 질문을 추가합니다.")
    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class},
            include = {"INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<String>> createDailyQuestions(@RequestBody AdminRequest.DailyQuestionsDTO request);
}
