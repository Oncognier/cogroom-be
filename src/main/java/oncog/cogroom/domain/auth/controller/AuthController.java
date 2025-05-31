package oncog.cogroom.domain.auth.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.AuthServiceRouter;
import oncog.cogroom.global.common.response.apiResponse;
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

    @PostMapping("/login")
    public ResponseEntity<apiResponse<LoginResponseDTO>> socialLogin(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO responseDTO = router.login(request);

        return ResponseEntity.ok(apiResponse.success(responseDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<apiResponse<SignupResponseDTO>> socialSignup(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO result = router.signup(request);

        return ResponseEntity.ok(apiResponse.success(result));
    }

    @PostMapping("/email-verification")
    public ResponseEntity<apiResponse<String>> sendEmail(@RequestParam String userEmail) throws MessagingException, IOException {
        emailService.sendEmail(userEmail);

        return ResponseEntity.ok(apiResponse.success());
    }

    @GetMapping("/check-verification")
    public ResponseEntity<apiResponse<Void>> verifyEmail(@RequestParam String userEmail,
                                                         @RequestParam String verificationCode) {
        emailService.verifyCode(userEmail,verificationCode);

        return ResponseEntity.ok(apiResponse.success());
    }

    @PostMapping("/email/{userEmail}/status")
    public ResponseEntity<apiResponse<Boolean>> checkEmailVerificationStatus(@PathVariable String userEmail) {
        boolean result = emailService.verifiedEmail(userEmail);

        return ResponseEntity.ok(apiResponse.success(result));
    }

    // 인가 코드 반환받을 테스트 컨트롤러
    @GetMapping("/login/code")
    public ResponseEntity<String> test(@RequestParam String code) {
        log.info("code = " + code);
        return ResponseEntity.ok("good");
    }
}
