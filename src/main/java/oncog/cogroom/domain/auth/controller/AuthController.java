package oncog.cogroom.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.AuthServiceRouter;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import oncog.cogroom.global.common.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceRouter router;
    private final EmailService emailService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    @Operation(summary = "소셜/로컬 통합 로그인", description = "소셜/로컬 통합 로그인 로직을 처리합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ApiResponse<LoginResponseDTO> socialLogin(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO result = router.login(request);

        // refreshToken 쿠키로 셋팅
        cookieUtil.addRefreshToken(response, result.getTokens().getRefreshToken());

        LoginResponseDTO responseExcludedRefreshToken = result.excludeRefreshToken();
//        return ResponseEntity.ok(ApiResponse.success(responseExcludedRefreshToken));
        return ApiResponse.success(ApiSuccessCode.SUCCESS, responseExcludedRefreshToken);
    }


    @PostMapping("/signup")
    @Operation(summary = "소셜/로컬 통합 회원가입", description = "소셜/로컬 통합 회원가입 로직을 처리합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ApiResponse<SignupResponseDTO> socialSignup(@RequestBody SignupRequestDTO request, HttpServletResponse response) {
        SignupResponseDTO result = router.signup(request);

        // refreshToken 쿠키로 셋팅
        cookieUtil.addRefreshToken(response, result.getTokens().getRefreshToken());

        SignupResponseDTO responseExcludedRefreshToken = result.excludeRefreshToken();
//        return ResponseEntity.ok(ApiResponse.success(responseExcludedRefreshToken));
        return ApiResponse.success(ApiSuccessCode.SUCCESS, responseExcludedRefreshToken);
    }

    @PostMapping("/email-verification")
    @Operation(summary = "인증 이메일 전송", description = "인증용 링크가 포함된 이메일을 전송합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ApiResponse<String> sendEmail(@RequestParam String userEmail) throws MessagingException, IOException {
        emailService.sendEmail(userEmail);

//        return ResponseEntity.ok(ApiResponse.success());
        return ApiResponse.success(ApiSuccessCode.SUCCESS);
    }

    @GetMapping("/check-verification")
    @Operation(summary = "이메일 인증", description = "링크가 클릭되었을 때 이메일을 인증합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ApiResponse<Void> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode) {
        emailService.verifyCode(userEmail,verificationCode);

//        return ResponseEntity.ok(ApiResponse.success());
        return ApiResponse.success(ApiSuccessCode.SUCCESS);
    }

    @PostMapping("/email/{userEmail}/status")
    @Operation(summary = "이메일 인증 여부 반환", description = "이메일의 인증이 완료되었는지 여부를 반환합니다. \n 응답 코드에 따른 자세한 결과는 Notion 명세서를 참고 부탁드립니다.")
    public ApiResponse<Boolean> checkEmailVerificationStatus(@PathVariable String userEmail) {
        boolean result = emailService.verifiedEmail(userEmail);

//        return ResponseEntity.ok(ApiResponse.success(result));
        return ApiResponse.success(ApiSuccessCode.SUCCESS, result);
    }

    // 인가 코드 반환받을 테스트 컨트롤러
    @GetMapping("/login/code")
    public ResponseEntity<String> test(@RequestParam String code) {
        log.info("code = " + code);
        return ResponseEntity.ok("good");
    }
}
