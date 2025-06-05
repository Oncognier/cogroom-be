package oncog.cogroom.global.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-token-expiration}")
    private String refreshExpiration;

    @Value("${jwt.access-token-expiration}")
    private String accessExpiration;

    public void addTokenForCookie(HttpServletResponse response, AuthResponseDTO.ServiceTokenDTO tokens) {
        if (tokens != null) {
            Cookie refreshToken = new Cookie("refreshToken", tokens.getRefreshToken());
            refreshToken.setHttpOnly(true);
            refreshToken.setPath("/");
            refreshToken.setMaxAge(Integer.parseInt(refreshExpiration));

            Cookie accessToken = new Cookie("accessToken", tokens.getAccessToken());
            accessToken.setPath("/");
            accessToken.setMaxAge(Integer.parseInt(accessExpiration));

            response.addCookie(refreshToken);
            response.addCookie(accessToken);
        }
    }
}
