package oncog.cogroom.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.OauthTokenResponseDTO;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.auth.userInfo.KakaoUserInfo;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KakaoAuthService extends AbstractSocialAuthService{
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    private final RestTemplate restTemplate;

    public KakaoAuthService(JwtProvider jwtProvider, MemberRepository memberRepository, RestTemplate restTemplate) {
        super(jwtProvider, memberRepository);
        this.restTemplate = restTemplate;
    }

    // 카카오 액세스 토큰 조회
    @Override
    protected String requestAccessToken(String code) {
       HttpEntity<MultiValueMap<String, String>> request = getHttpEntityForToken(code);

        ResponseEntity<OauthTokenResponseDTO.KakaoTokenDTO> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                OauthTokenResponseDTO.KakaoTokenDTO.class
        );

        log.info("카카오 토큰 응답: {}", response.getBody());

        return response.getBody().getAccessToken();
    }

    // 카카오 사용자 정보 조회
    @Override
    protected SocialUserInfo requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            SocialResponseDTO.KakaoUserResponseDTO responseDTO = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v2/user/me",
                    new HttpEntity<>(headers),
                    SocialResponseDTO.KakaoUserResponseDTO.class
            ).getBody();

            return new KakaoUserInfo(responseDTO);

        } catch (HttpClientErrorException e) {
            log.error("Kakao 사용자 정보 조회 API 호출 실패: 상태 코드 {}, 응답 본문 {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("not found");
        }
    }

    @Override
    protected Provider getProvider() {
        return Provider.KAKAO;
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntityForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("code", code);

        return new HttpEntity<>(params, headers);
    }

}
