package oncog.cogroom.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.MemberInfoDTO;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class memberController {

    private final MemberService memberService;
    @GetMapping("/")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> getMemberInfo() {
        MemberInfoDTO memberInfo = memberService.findMemberInfo();

        return ResponseEntity.ok(ApiResponse.success(memberInfo));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(MemberRequestDTO.MemberInfoUpdateDTO request) {
        memberService.updateMemberInfo(request);

        return ResponseEntity.ok(ApiResponse.success());
    }

}
