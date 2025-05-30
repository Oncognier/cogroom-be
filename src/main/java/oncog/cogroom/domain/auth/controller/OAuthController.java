package oncog.cogroom.domain.auth.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.OAuthServiceRouter;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.global.common.response.apiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class OAuthController {

    private final OAuthServiceRouter router;
    private final EmailService emailService;

    @PostMapping("/social-login")
    public ResponseEntity<apiResponse<SocialResponseDTO.LoginResponseDTO>> socialLogin(@RequestParam Provider provider,
                                                                          @RequestParam String authorizationCode) {
        SocialResponseDTO.LoginResponseDTO responseDTO = router.login(provider, authorizationCode);

        return ResponseEntity.ok(apiResponse.success(responseDTO));
    }


    @PostMapping("/send-verification")
    public ResponseEntity<apiResponse<String>> sendEmail(@RequestParam String userEmail) throws MessagingException, IOException {
        emailService.sendEmail(userEmail);

        return ResponseEntity.ok(apiResponse.success());
    }

    @GetMapping("/check-verification")
    public ResponseEntity<apiResponse<Void>> verifyEmail(@RequestParam String verificationCode) {
        return ResponseEntity.ok(apiResponse.success());

    }
    // 인가 코드 반환받을 테스트 컨트롤러
    @GetMapping("/login/code")
    public ResponseEntity<String> test(@RequestParam String code) {
        log.info("code = " + code);
        return ResponseEntity.ok("good");
    }
}
