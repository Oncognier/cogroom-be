package oncog.cogroom.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.request.AuthRequestDTO;
import oncog.cogroom.domain.auth.service.AuthServiceRouter;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.common.util.CookieUtil;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExample;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.LoginRequestDTO;
import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.SignupRequestDTO;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.LoginResponseDTO;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.SignupResponseDTO;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceRouter router;
    private final EmailService emailService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class},
            include = {"USER_NOT_FOUND", "DUPLICATE_USER_EMAIL", "DUPLICATE_USER_NICKNAME"})
    @Operation(summary = "소셜/로컬 통합 로그인", description = "소셜/로컬 통합 로그인 로직을 처리합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> socialLogin(@RequestBody @Valid LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO result = router.login(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        LoginResponseDTO responseExcludedToken = result.excludeTokens();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }


    @PostMapping("/signup")
    @ApiErrorCodeExample(
            value = ApiErrorCode.class,
            include = {"USER_NOT_FOUND", "DUPLICATE_USER_EMAIL"}
    )
    @Operation(summary = "소셜/로컬 통합 회원가입", description = "소셜/로컬 통합 회원가입 로직을 처리합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<SignupResponseDTO>> socialSignup(@RequestBody @Valid SignupRequestDTO request, HttpServletResponse response) {
        SignupResponseDTO result = router.signup(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        SignupResponseDTO responseExcludedToken = result.excludeTokens();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }

    @PostMapping("/email-verification")
    @Operation(summary = "인증 이메일 전송", description = "인증용 링크가 포함된 이메일을 전송합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request) throws MessagingException, IOException {
        emailService.existEmail(request.getEmail());

        emailService.sendEmail(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));

    }

    @GetMapping("/check-verification")
    @Operation(summary = "이메일 인증", description = "링크가 클릭되었을 때 이메일을 인증합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode) {
        emailService.verifyCode(userEmail,verificationCode);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/email/status")
    @Operation(summary = "이메일 인증 여부 반환", description = "이메일의 인증이 완료되었는지 여부를 반환합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request) {
        boolean result = emailService.verifiedEmail(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, result));

    }
}
