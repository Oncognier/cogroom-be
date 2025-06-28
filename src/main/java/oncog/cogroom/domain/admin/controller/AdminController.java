package oncog.cogroom.domain.admin.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.controller.docs.AdminControllerDocs;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.service.AdminAuthService;
import oncog.cogroom.domain.admin.service.AdminDailyService;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.PageResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminControllerDocs {

    private final AdminDailyService adminDailyService;
    private final AdminAuthService adminAuthService;
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<PageResponse<AdminResponse.MemberListDTO>>> getMemberList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<AdminResponse.MemberListDTO> result = adminAuthService.findMemberList(pageable, startDate, endDate, keyword);
        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, result));
    }

    @GetMapping("/daily/questions")
    public ResponseEntity<ApiResponse<PageResponse<AdminResponse.DailyQuestionsDTO>>> getDailyQuestions(
            @RequestParam(required = false) List<Integer> category,
            @RequestParam(required = false) List<String> level,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<AdminResponse.DailyQuestionsDTO> response = adminDailyService.getDailyQuestions(pageable, category, level, keyword);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }

    @PostMapping("/daily/questions")
    public ResponseEntity<ApiResponse<String>> createDailyQuestions(@RequestBody AdminRequest.DailyQuestionsDTO request) {
        adminDailyService.createDailyQuestions(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @DeleteMapping("/members")
    public ResponseEntity<ApiResponse<Void>> deleteMembers(@RequestBody AdminRequest.DeleteMembersDTO request){
        adminAuthService.deleteMembers(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PatchMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(@PathVariable Long memberId,
                                                              @RequestParam MemberRole role) {
        adminAuthService.updateMemberRole(memberId, role);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @GetMapping("/members/{memberId}/daily")
    public ResponseEntity<ApiResponse<PageResponse<AdminResponse.MemberDailyListDTO>>> getDailyContents(
            @PathVariable Long memberId,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<QuestionLevel> questionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate,
            @PageableDefault(size = 5, sort = "answeredAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<AdminResponse.MemberDailyListDTO> result = adminDailyService.getDailyContents(
                memberId, pageable, category, keyword, questionLevel,startDate,endDate);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, result));

    }
}
