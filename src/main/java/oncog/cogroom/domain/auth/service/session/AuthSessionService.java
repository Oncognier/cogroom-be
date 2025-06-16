package oncog.cogroom.domain.auth.service.session;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.common.util.TokenUtil;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthSessionService extends BaseService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenUtil tokenUtil;
    private final MemberRepository memberRepository;

    // 토큰 재발급 API
    public AuthResponseDTO.ServiceTokenDTO reIssue(String refreshToken) {

        // refresh token 검증
        jwtProvider.isValid(refreshToken);

        Long memberId = jwtProvider.extractMemberId(refreshToken);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        // 토큰 생성
        AuthResponseDTO.ServiceTokenDTO tokenDTO = tokenUtil.createTokens(member);

        // 토큰 갱신
        tokenRotation(tokenDTO.getRefreshToken(), memberId);

        log.info("토큰 재발급 호출");

        return tokenDTO;
    }

    public void logout(HttpServletRequest request) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        // 사용자 ID 추출
        Long memberId = jwtProvider.extractMemberId();

        // accessToken 추출
        String accessToken = jwtProvider.resolveToken(request);

        // BlackList에 저장할 Key 생성
        String key = "BL:" + DigestUtils.sha256Hex(accessToken);

        // accessToken의 TTL 계산
        Date expiration = jwtProvider.getExpiration(accessToken);
        long remainTime = expiration.getTime() - System.currentTimeMillis();

        log.info("remainTime : " + remainTime);

        // 블랙 리스트 설정
        values.set(key, accessToken, remainTime, TimeUnit.MILLISECONDS);

        // refreshToken 삭제
        redisTemplate.delete("RT:" + memberId);
    }

    // RefreshToken 갱신
    private void tokenRotation(String refreshToken,Long memberId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set("RT:" + memberId, refreshToken, refreshExpiration, TimeUnit.DAYS);
    }

}