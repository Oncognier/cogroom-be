package oncog.cogroom.global.security.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.auth.exception.AuthException;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecretKey;
    @Value("${jwt.access-token-expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    public String generateAccessToken(CustomUserDetails userDetails){
        return Jwts.builder()
                .subject(String.valueOf(userDetails.getMemberId()))
                .claim("role", userDetails.getRole())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(accessExpiration)))
                .signWith(generateSecretKey())
                .compact();
    }
    public String generateRefreshToken(String memberId) {
        return Jwts.builder()
                .subject(memberId)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(refreshExpiration)))
                .signWith(generateSecretKey())
                .compact();
    }

    public boolean isValid(String token) {
        try{
            // 토큰 유효성 검사
            Jwts.parser().verifyWith(generateSecretKey()).build()
                    .parseSignedClaims(token);

            // 블랙리스트 검사
            String key = "BL:" + DigestUtils.sha256Hex(token);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                log.warn("JWT Token is blackListed: {}", token);
                throw new AuthException(AuthErrorCode.IN_BLACK_LIST);
            }

            return true;
        }catch(ExpiredJwtException e){
            log.error("Expired JWT token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token : {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(generateSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public Long extractMemberId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getMemberId();
    }

    // accessToken 만료시 사용
    public Long extractMemberId(String refreshToken) {
        return Long.valueOf(getClaims(refreshToken).getSubject());
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
    private SecretKey generateSecretKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }
}