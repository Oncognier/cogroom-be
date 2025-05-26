package oncog.cogroom.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.OauthTokenResponseDTO;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService extends AbstractSocialAuthService{
    @Value("${oauth.kakao.client_id}")
    private String clientId;

    @Value("${oauth.kakao.client_secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public OauthTokenResponseDTO.KakaoTokenDTO getTokens(String code) {

        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(code);

        ResponseEntity<OauthTokenResponseDTO.KakaoTokenDTO> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                OauthTokenResponseDTO.KakaoTokenDTO.class
        );

        log.info("카카오 토큰 응답: {}", response.getBody());
        return response.getBody();
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("code", code);

        if (!clientSecret.isBlank()) {
            params.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return request;
    }

    @Override
    protected SocialResponseDTO.LoginResponseDTO login(String authCode) {
        OauthTokenResponseDTO.KakaoTokenDTO tokens = getTokens(authCode);

        String providerId = tokens.getIdToken().getProviderId();

        // 존재하지 않는 회원인 경우
        Optional<Member> member = memberRepository.findByProviderId(providerId);
        if (member.isEmpty()) {
            return SocialResponseDTO.LoginResponseDTO.builder()
                    .email(tokens.getIdToken().getEmail())
                    .nickname(tokens.getIdToken().getEmail())
                    .needSignup(true)
                    .tokens(null)
                    .build();
        }

        return SocialResponseDTO.LoginResponseDTO.builder()
                .email(null)
                .nickname(null)
                .tokens(createTokens(member))
                .needSignup(false)
                .build();
    }

    private SocialResponseDTO.ServiceTokenDTO createTokens(Optional<Member> member) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .provider(Provider.KAKAO)
                .role(MemberRole.USER)
                .memberId(member.get().getId())
                .memberEmail(member.get().getEmail())
                .build();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getMemberEmail());

        return SocialResponseDTO.ServiceTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    @Override
    protected Provider getProvider() {
        return Provider.KAKAO;
    }
}
