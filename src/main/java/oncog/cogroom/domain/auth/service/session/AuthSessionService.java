package oncog.cogroom.domain.auth.service.session;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.AuthResponse;
import oncog.cogroom.domain.auth.service.TokenService;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.common.util.TokenUtil;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthSessionService extends BaseService {


    private final JwtProvider jwtProvider;
    private final TokenUtil tokenUtil;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    // 토큰 재발급 API
    public AuthResponse.ServiceTokenDTO reIssue(String refreshToken) {

        // refresh token 검증
        jwtProvider.isValid(refreshToken);

        Long memberId = jwtProvider.extractMemberId(refreshToken);

        tokenService.checkTokenInRedis(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        // 토큰 생성
        AuthResponse.ServiceTokenDTO tokenDTO = tokenUtil.createTokens(member);

        // 토큰 갱신
        tokenService.tokenRotation(tokenDTO.getRefreshToken(), memberId);

        log.info("토큰 재발급 호출");

        return tokenDTO;
    }

    public void logout(HttpServletRequest request) {

        // 사용자 ID 추출
        Long memberId = jwtProvider.extractMemberId();

        // accessToken 추출
        String accessToken = jwtProvider.resolveToken(request);

        tokenService.expireToken(accessToken,  memberId);
    }




}