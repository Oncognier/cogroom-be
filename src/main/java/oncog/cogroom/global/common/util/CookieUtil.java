package oncog.cogroom.global.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-token-expiration}")
    private String refreshExpiration;

    public void addRefreshToken(HttpServletResponse response, String token) {
        Cookie refreshToken = new Cookie("refreshToken", token);
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(Integer.parseInt(refreshExpiration));

        response.addCookie(refreshToken);
    }
}
