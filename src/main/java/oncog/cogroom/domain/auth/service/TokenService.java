package oncog.cogroom.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.auth.exception.AuthException;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    // refresh Token이 redis에 존재하는지 검사
    public void checkTokenInRedis(Long memberId) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey("RT:" + memberId)))
            throw new AuthException(AuthErrorCode.TOKEN_INVALID_ERROR);
    }

    // RefreshToken 갱신
    public void tokenRotation(String refreshToken, Long memberId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set("RT:" + memberId, refreshToken, refreshExpiration, TimeUnit.DAYS);
    }

    // 로그아웃 & 회원 탈퇴시 액세스 토큰 블랙리스트, 리프레시 토큰 삭제 처리
    public void expireToken(String accessToken, Long memberId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        // BlackList에 저장할 Key 생성
        String key = "BL:" + DigestUtils.sha256Hex(accessToken);

        // accessToken의 TTL 계산
        Date expiration = jwtProvider.getExpiration(accessToken);
        long remainTime = expiration.getTime() - System.currentTimeMillis();

        // 블랙 리스트 설정
        values.set(key, accessToken, remainTime, TimeUnit.MILLISECONDS);

        // refreshToken 삭제
        redisTemplate.delete("RT:" + memberId);
    }

}
