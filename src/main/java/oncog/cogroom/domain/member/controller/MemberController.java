package oncog.cogroom.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.docs.MemberControllerDocs;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.dto.MemberResponseDTO;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.*;
import static oncog.cogroom.domain.member.dto.MemberResponseDTO.MemberInfoDTO;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members/me")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> getMemberInfo() {
        MemberInfoDTO memberInfo = memberService.findMemberInfo();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberInfo));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MemberSummaryDTO>> getMemberSummary() {
        MemberSummaryDTO memberSummary = memberService.findMemberSummary();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberSummary));
    }

    @PatchMapping("")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(@RequestBody @Valid MemberRequestDTO.MemberInfoUpdateDTO request) {
        memberService.updateMemberInfo(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/nickname")
    public ResponseEntity<ApiResponse<Boolean>> existNickname(@RequestBody @Valid MemberRequestDTO.ExistNicknameDTO request) {
        boolean isExist = memberService.existNickname(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, isExist));
    }

}
