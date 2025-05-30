package oncog.cogroom.domain.auth.service;


import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import oncog.cogroom.global.security.jwt.JwtProvider;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractSocialAuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    // 소셜 로그인 공통 로직
    public final SocialResponseDTO.LoginResponseDTO login(String code){
        String accessToken = requestAccessToken(code);
        SocialUserInfo userInfo = requestUserInfo(accessToken);

        // provider와 ProviderId를 복합 유니크 키로 검사하여 사용자 조회
        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(getProvider(), userInfo.getProviderId());

        // 사용자 유무에 따른 로직 분기
        if (memberOpt.isPresent()) {
            SocialResponseDTO.ServiceTokenDTO tokenDTO = createTokens(memberOpt.get());

            return SocialResponseDTO.LoginResponseDTO.builder()
                    .socialUserInfo(null)
                    .tokens(tokenDTO)
                    .needSignup(false)
                    .build();
        }else{
            return SocialResponseDTO.LoginResponseDTO.builder()
                    .socialUserInfo(userInfo)
                    .tokens(null)
                    .needSignup(true)
                    .build();
        }
    }

    // 토큰 생성
    public final SocialResponseDTO.ServiceTokenDTO createTokens(Member member) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                        .provider(Provider.KAKAO)
                        .role(MemberRole.USER)
                        .memberId(member.getId())
                        .build();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(userDetails.getMemberId()));

        return SocialResponseDTO.ServiceTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    // 각 구현체에서 오버라이드된 메소드들이 실행됨
    protected abstract String requestAccessToken(String code);

    protected abstract SocialUserInfo requestUserInfo(String accessToken);
    protected abstract Provider getProvider();

}
