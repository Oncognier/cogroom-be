package oncog.cogroom.domain.auth.service.session;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthResponseDTO.ServiceTokenDTO reIssue(String refreshToken) {

        // refresh token 검증
        jwtProvider.isValid(refreshToken);

        Long memberId = jwtProvider.extractMemberId(refreshToken);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 토큰 생성
        AuthResponseDTO.ServiceTokenDTO tokenDTO = tokenUtil.createTokens(member);

        // 토큰 갱신
        tokenRotation(tokenDTO.getRefreshToken(), memberId);

        return tokenDTO;
    }

    // RefreshToken 갱신
    private void tokenRotation(String refreshToken,Long memberId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set("RT:" + memberId, refreshToken, refreshExpiration, TimeUnit.DAYS);
    }
    
}
