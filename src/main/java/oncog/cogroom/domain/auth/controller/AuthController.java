package oncog.cogroom.domain.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.controller.docs.AuthControllerDocs;
import oncog.cogroom.domain.auth.dto.request.AuthRequestDTO;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import oncog.cogroom.domain.auth.service.AuthServiceRouter;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.session.AuthSessionService;
import oncog.cogroom.domain.daily.service.DailyQuestionAssignService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.common.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthServiceRouter router;
    private final AuthSessionService authSessionService;
    private final EmailService emailService;
    private final CookieUtil cookieUtil;
    private final DailyQuestionAssignService dailyQuestionAssignService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO.LoginResultDTO>> login(@RequestBody @Valid AuthRequestDTO.LoginRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO.LoginResultDTO result = router.login(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        // response body 토큰 제거
        AuthResponseDTO.LoginResultDTO responseExcludedToken = result.excludeTokens();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponseDTO.SignupResultDTO>> signup(@RequestBody @Valid AuthRequestDTO.SignupDTO request, HttpServletResponse response) throws MessagingException {
        AuthResponseDTO.SignupResultDTO result = router.signup(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        // response body 토큰 제거
        AuthResponseDTO.SignupResultDTO responseExcludedToken = result.excludeTokens();

        // 가입 후 질문 할당
        dailyQuestionAssignService.assignDailyQuestionAtSignup(request.getProvider(), request.getProviderId());

        // 웰컴 메일 발송
        emailService.sendWelcomeEmail(request.getEmail());

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        authSessionService.logout(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));

    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> reIssue(@CookieValue String refreshToken,
                                                     HttpServletResponse response) {
        // 토큰 재발급
        AuthResponseDTO.ServiceTokenDTO tokenDTO = authSessionService.reIssue(refreshToken);

        // 토큰 쿠키 & 헤더 셋팅
        cookieUtil.addTokenForCookie(response, tokenDTO);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }


    @PostMapping("/email-verification")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequestDTO.EmailDTO request) throws MessagingException, IOException {
        // 이메일 중복 검사
        emailService.existEmail(request.getEmail());

        // 중복 검사 이후 이메일 전송
        emailService.sendAuthCodeEmail(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));

    }

    @GetMapping("/check-verification")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode) {
        emailService.verifyCode(userEmail,verificationCode);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/email/status")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequestDTO.EmailDTO request) {
        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, emailService.verifiedEmail(request)));

    }
}
