package oncog.cogroom.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.MemberInfoDTO;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class memberController {

    private final MemberService memberService;
    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> getMemberInfo() {
        MemberInfoDTO memberInfo = memberService.findMemberInfo();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberInfo));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(@RequestBody MemberRequestDTO.MemberInfoUpdateDTO request) {
        memberService.updateMemberInfo(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

}
