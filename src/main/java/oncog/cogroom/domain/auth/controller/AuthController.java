package oncog.cogroom.domain.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.docs.AuthControllerDocs;
import oncog.cogroom.domain.auth.dto.request.AuthRequestDTO;
import oncog.cogroom.domain.auth.service.AuthServiceRouter;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.daily.service.DailyQuestionAssignService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.common.util.CookieUtil;

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
public class AuthController implements AuthControllerDocs {

    private final AuthServiceRouter router;
    private final EmailService emailService;
    private final CookieUtil cookieUtil;
    private final DailyQuestionAssignService dailyQuestionAssignService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> socialLogin(@RequestBody @Valid LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO result = router.login(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        LoginResponseDTO responseExcludedToken = result.excludeTokens();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDTO>> socialSignup(@RequestBody @Valid SignupRequestDTO request, HttpServletResponse response) {
        SignupResponseDTO result = router.signup(request);

        // Token 쿠키로 셋팅
        cookieUtil.addTokenForCookie(response, result.getTokens());

        SignupResponseDTO responseExcludedToken = result.excludeTokens();

        dailyQuestionAssignService.assignDailyQuestionAtSignup(request.getProvider(), request.getProviderId());

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, responseExcludedToken));

    }

    @PostMapping("/email-verification")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request) throws MessagingException, IOException {
        // 이메일 중복 검사
        emailService.existEmail(request.getEmail());

        // 중복 검사 이후 이메일 전송
        emailService.sendEmail(request);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));

    }

    @GetMapping("/check-verification")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode) {
        emailService.verifyCode(userEmail,verificationCode);

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS));
    }

    @PostMapping("/email/status")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@RequestBody @Valid AuthRequestDTO.EmailRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, emailService.verifiedEmail(request)));

    }
}
