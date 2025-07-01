package oncog.cogroom.domain.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.controller.docs.AuthControllerDocs;
import oncog.cogroom.domain.auth.dto.request.AuthRequest;
import oncog.cogroom.domain.auth.dto.response.AuthResponse;
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
    public ResponseEntity<ApiResponse<AuthResponse.LoginResultDTO>> login(@RequestBody @Valid AuthRequest.LoginDTO request, HttpServletResponse response) {
        AuthResponse.LoginResultDTO result = router.login(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        // response body 토큰 제거
        AuthResponse.LoginResultDTO responseExcludedToken = result.excludeTokens();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse.SignupResultDTO>> signup(@RequestBody @Valid AuthRequest.SignupDTO request, HttpServletResponse response) throws MessagingException {
        AuthResponse.SignupResultDTO result = router.signup(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        // response body 토큰 제거
        AuthResponse.SignupResultDTO responseExcludedToken = result.excludeTokens();

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
        AuthResponse.ServiceTokenDTO tokenDTO = authSessionService.reIssue(refreshToken);

        // 토큰 쿠키 & 헤더 셋팅
        cookieUtil.addTokenForCookie(response, tokenDTO);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }


    @PostMapping("/email-verification")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequest.EmailDTO request) throws MessagingException, IOException {
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
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequest.EmailDTO request) {
        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, emailService.verifiedEmail(request)));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@RequestBody AuthRequest.WithdrawDTO request,
                                                            @RequestHeader("Authorization") String accessToken) {
        router.withdraw(request, accessToken);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }
}
