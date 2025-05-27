package oncog.cogroom.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public class OAuthController {

    @GetMapping("/oauth/callback")
    public HttpServletResponse getCookie(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam boolean needSignup,
                                            @RequestParam String nickname,
                                            @RequestParam String email) {
        log.info("nickname : " + nickname);
        log.info("email : " + email);
        log.info("needSignup : " + needSignup);
        log.info("cookies : " + request.getCookies());

        response.addCookie(new Cookie("accessToken", request.getCookies()[0].toString()));
        response.addCookie(new Cookie("refreshToken", request.getCookies()[1].toString()));
        return response;
    }
}
