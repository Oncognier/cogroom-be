package oncog.cogroom.domain.auth.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import oncog.cogroom.domain.auth.dto.request.AuthRequestDTO;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExample;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "Auth", description = "Auth 관련 API")
public interface AuthControllerDocs {

    @ApiErrorCodeExample(
            value = AuthErrorCode.class,
            include = {"KAKAO_AUTH_FAILED", "KAKAO_INVALID_AUTHORIZATION_CODE"})
    @Operation(summary = "소셜/로컬 통합 로그인", description = "소셜/로컬 통합 로그인 로직을 처리합니다.")
    public ResponseEntity<ApiResponse<AuthResponseDTO.LoginResponseDTO>> socialLogin(@RequestBody @Valid AuthRequestDTO.LoginRequestDTO request, HttpServletResponse response);


    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"EMPTY_FILED", "INVALID_EMAIL_FOTMAT"})
    @Operation(summary = "소셜/로컬 통합 회원가입", description = "소셜/로컬 통합 회원가입 로직을 처리합니다. ")
    public ResponseEntity<ApiResponse<AuthResponseDTO.SignupResponseDTO>> socialSignup(@RequestBody @Valid AuthRequestDTO.SignupRequestDTO request, HttpServletResponse response);

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"EMPTY_FILED", "INVALID_EMAIL_FOTMAT", "ALREADY_EXIST_EMAIL"})
    @Operation(summary = "인증 이메일 전송", description = "인증용 링크가 포함된 이메일을 전송합니다. ")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request) throws MessagingException, IOException;

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"EMPTY_FILED", "EXPIRED_LINK"})
    @Operation(summary = "이메일 인증", description = "링크가 클릭되었을 때 이메일을 인증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode);

    @Operation(summary = "이메일 인증 여부 반환", description = "이메일의 인증이 완료되었는지 여부를 반환합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request);
}
