package oncog.cogroom.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import oncog.cogroom.domain.auth.dto.request.AuthRequestDTO;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExample;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "Auth", description = "Auth 관련 API")
public interface AuthControllerDocs {

    @ApiErrorCodeExample(
            value = AuthErrorCode.class,
            include = {"KAKAO_REQUEST_ERROR"})
    @Operation(summary = "소셜/로컬 통합 로그인", description = "소셜/로컬 통합 로그인 로직을 처리합니다.")
    public ResponseEntity<ApiResponse<AuthResponseDTO.LoginResultDTO>> login(@RequestBody @Valid AuthRequestDTO.LoginRequestDTO request, HttpServletResponse response);


    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class,MemberErrorCode.class, ApiErrorCode.class},
            include = {"EMAIL_PATTERN_ERROR", "PHONENUMBER_PATTERN_ERROR", "PASSWORD_PATTERN_ERROR", "NICKNAME_INVALID_PATTERN","SIZE_ERROR",
            "EMPTY_FIELD_ERROR"})
    @Operation(summary = "소셜/로컬 통합 회원가입", description = "소셜/로컬 통합 회원가입 로직을 처리합니다. ")
    public ResponseEntity<ApiResponse<AuthResponseDTO.SignupResultDTO>> signup(@RequestBody @Valid AuthRequestDTO.SignupDTO request, HttpServletResponse response) throws MessagingException;

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"EMAIL_PATTERN_ERROR", "ALREADY_EXIST_EMAIL", "EMPTY_FILED_ERROR"})
    @Operation(summary = "인증 이메일 전송", description = "인증용 링크가 포함된 이메일을 전송합니다. ")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequestDTO.EmailDTO request) throws MessagingException, IOException;

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"EMAIL_PATTERN_ERROR", "EXPIRED_LINK"})
    @Operation(summary = "이메일 인증", description = "링크가 클릭되었을 때 이메일을 인증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode);

    @Operation(summary = "이메일 인증 여부 반환", description = "이메일의 인증이 완료되었는지 여부를 반환합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequestDTO.EmailDTO request);

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, MemberErrorCode.class},
            include = {"INVALID_TOKEN", "IN_BLACK_LIST", "EXPIRED_TOKEN", "MEMBER_NOT_FOUND"}
    )
    @Operation(summary = "토큰 갱신 API", description = "토큰 재발급 API 입니다.")
    public ResponseEntity<ApiResponse<Void>> reIssue(@CookieValue String refreshToken,
                                                     HttpServletResponse response);

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, MemberErrorCode.class},
            include = {"INVALID_TOKEN", "IN_BLACK_LIST", "EXPIRED_TOKEN", "MEMBER_NOT_FOUND"}
    )
    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다.")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request);
}
