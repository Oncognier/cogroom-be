package oncog.cogroom.global.common.util;

import jakarta.servlet.http.HttpServletResponse;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-token-expiration}")
    private String refreshExpiration;

    @Value("${jwt.access-token-expiration}")
    private String accessExpiration;

    //secure 옵션 추가 필요 (https 설정 이후)
    public void addTokenForCookie(HttpServletResponse response, AuthResponseDTO.ServiceTokenDTO tokens) {
        if (tokens != null) {
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken",tokens.getRefreshToken())
                    .httpOnly(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(Integer.parseInt(refreshExpiration))
                    .build();

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken",tokens.getAccessToken())
                    .httpOnly(false)
                    .sameSite("None")
                    .path("/")
                    .maxAge(Integer.parseInt(accessExpiration))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        }
    }
}
