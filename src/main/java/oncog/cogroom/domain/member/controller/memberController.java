package oncog.cogroom.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExample;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
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
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class},
            include = {"INVALID_TOKEN","EXPIRED_TOKEN", "MEMBER_NOT_FOUND"})
    @Operation(summary = "사용자 정보 조회 API", description = "사용자 정보 수정을 위해 사용자 정보를 조회합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> getMemberInfo() {
        MemberInfoDTO memberInfo = memberService.findMemberInfo();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS,memberInfo));
    }

    @PatchMapping("/me")
    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class},
            include = {"INVALID_TOKEN","EXPIRED_TOKEN", "MEMBER_NOT_FOUND",
                    "INVALID_PHONE_NUMBER", "INVALID_PASSWORD_FORMAT", "DUPLICATE_USER_NICKNAME"})
    @Operation(summary = "사용자 정보 수정 API", description = "사용자 정보를 수정합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(@RequestBody @Valid MemberRequestDTO.MemberInfoUpdateDTO request) {
        memberService.updateMemberInfo(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/me/nickname")
    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class, MemberErrorCode.class},
            include = {"EMPTY_FILED", "DUPLICATE_USER_NICKNAME"})
    @Operation(summary = "닉네임 중복검사 API", description = "닉네임 중복 검사를 진행합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<Boolean>> existNickname(@RequestBody @Valid MemberRequestDTO.ExistNicknameDTO request) {
        boolean isExist = memberService.existNickname(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, isExist));
    }

}
