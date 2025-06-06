package oncog.cogroom.domain.auth.service.social;


import lombok.RequiredArgsConstructor;
import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.*;
import oncog.cogroom.domain.auth.service.AuthService;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.util.TokenUtil;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import oncog.cogroom.global.security.jwt.JwtProvider;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractSocialAuthService implements AuthService {

    private final MemberRepository memberRepository;
    private final TokenUtil tokenUtil;

    // 소셜 로그인 공통 로직
    public final LoginResponseDTO login(LoginRequestDTO request){
        String accessToken = requestAccessToken(request.getCode());
        SocialUserInfo userInfo = requestUserInfo(accessToken);

        // provider와 ProviderId를 복합 유니크 키로 검사하여 사용자 조회
        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(request.getProvider(), userInfo.getProviderId());

        // 사용자 유무에 따른 로직 분기
        if (memberOpt.isPresent()) {
            ServiceTokenDTO tokenDTO = tokenUtil.createTokens(memberOpt.get());

            return LoginResponseDTO.builder()
                    .socialUserInfo(null)
                    .tokens(tokenDTO)
                    .needSignup(false)
                    .build();
        }else{
            return LoginResponseDTO.builder()
                    .socialUserInfo(userInfo)
                    .tokens(null)
                    .needSignup(true)
                    .build();
        }
    }

    // 소셜 로그인의 경우 회원가입 클릭 -> 로그인 처리가 ui적으로 깔끔하기에 토큰 발급
    public final SignupResponseDTO signup(SignupRequestDTO request) {
        Member savedMember = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(MemberRole.USER)
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .description(null)
                .phoneNumber(null)
                .profileImageUrl(null)
                .status(MemberStatus.ACTIVE)
                .build());

        ServiceTokenDTO tokens = tokenUtil.createTokens(savedMember);

        return SignupResponseDTO.builder()
                .tokens(tokens)
                .build();
    }


    // 각 구현체에서 오버라이드된 메소드들이 실행됨
    protected abstract String requestAccessToken(String code);

    protected abstract SocialUserInfo requestUserInfo(String accessToken);

}
