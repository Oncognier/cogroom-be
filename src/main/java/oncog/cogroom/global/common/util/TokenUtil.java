package oncog.cogroom.global.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.AuthResponseDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUtil {
    private final JwtProvider jwtProvider;

    // 토큰 생성
    public final AuthResponseDTO.ServiceTokenDTO createTokens(Member member) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .provider(member.getProvider())
                .role(MemberRole.USER)
                .memberId(member.getId())
                .build();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(userDetails.getMemberId()));

        return AuthResponseDTO.ServiceTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }
}
