package oncog.cogroom.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.request.AuthRequest;
import oncog.cogroom.domain.daily.dto.response.DailyResponse;
import oncog.cogroom.domain.daily.service.DailyService;
import oncog.cogroom.domain.member.controller.docs.MemberControllerDocs;
import oncog.cogroom.domain.member.dto.request.MemberRequest;
import oncog.cogroom.domain.member.dto.response.MemberResponse;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members/me")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final DailyService dailyService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberResponse.MemberInfoDTO>> getMemberInfo() {
        MemberResponse.MemberInfoDTO memberInfo = memberService.findMemberInfo();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberInfo));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MemberResponse.MemberSummaryDTO>> getMemberSummary() {
        MemberResponse.MemberSummaryDTO memberSummary = memberService.findMemberSummary();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberSummary));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<MemberResponse.MemberMyPageInfoDTO>> getMemberInfoForMyPage() {
        MemberResponse.MemberMyPageInfoDTO memberForMyPage = memberService.findMemberForMyPage();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberForMyPage));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyResponse.AssignedQuestionWithAnswerDTO>>> getDailyQuestionAndAnswer() {
        List<DailyResponse.AssignedQuestionWithAnswerDTO> response = dailyService.getAssignedAndAnsweredQuestion();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, response));
    }

    @PatchMapping("")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(@RequestBody @Valid MemberRequest.MemberInfoUpdateDTO request) {
        memberService.updateMemberInfo(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/nickname")
    public ResponseEntity<ApiResponse<Boolean>> existNickname(@RequestBody @Valid MemberRequest.ExistNicknameDTO request) {
        boolean isExist = memberService.existNickname(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, isExist));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@RequestBody MemberRequest.WithdrawDTO request,
                                                            HttpServletRequest servletRequest) {
        memberService.withdraw(request, servletRequest);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }
}
