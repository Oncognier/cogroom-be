package oncog.cogroom.domain.auth.service.social;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.response.SocialTokenResponse;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.auth.repository.WithdrawReasonRepository;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.TokenService;
import oncog.cogroom.domain.auth.userInfo.KakaoUserInfo;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.util.TokenUtil;
import oncog.cogroom.domain.auth.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import static oncog.cogroom.domain.auth.dto.response.SocialUserInfo.KakaoUserInfoDTO;
@Service
@Slf4j
public class KakaoAuthService extends AbstractAuthService {
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    private final RestTemplate restTemplate;

    public KakaoAuthService(MemberRepository memberRepository,
                            TokenUtil tokenUtil,
                            EmailService emailService,
                            RestTemplate restTemplate,
                            RedisTemplate<String, String> redisTemplate) {
        super( memberRepository, emailService ,tokenUtil, redisTemplate);
        this.restTemplate = restTemplate;
    }
    // 카카오 액세스 토큰 조회
    @Override
    protected String requestAccessToken(String code) {
        try {
            HttpEntity<MultiValueMap<String, String>> request = getHttpEntityForToken(code);

            ResponseEntity<SocialTokenResponse.KakaoTokenDTO> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    request,
                    SocialTokenResponse.KakaoTokenDTO.class
            );

            log.info("카카오 토큰 응답: {}", response.getBody());

            return response.getBody().getAccessToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("카카오 액세스 토큰 요청 실패 : {}", e.getResponseBodyAsString());
            throw new AuthException(AuthErrorCode.KAKAO_REQUEST_ERROR);
        }
    }

    // 카카오 사용자 정보 조회
    @Override
    protected SocialUserInfo requestUserInfo(String accessToken) {
        try {
            HttpEntity<Void> request = createBearerRequest(accessToken);

            KakaoUserInfoDTO responseDTO = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v2/user/me",
                    request,
                    KakaoUserInfoDTO.class
            ).getBody();

            return new KakaoUserInfo(responseDTO);

        } catch (HttpClientErrorException e) {
            log.error("Kakao 사용자 정보 조회 API 호출 실패: 상태 코드 {}, 응답 본문 {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AuthException(AuthErrorCode.KAKAO_REQUEST_ERROR);
        }
    }

    @Override
    public void unlink(Member member) {
        HttpHeaders headers = createAdminHeader();

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", member.getProviderId());

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/user/unlink",
                request,
                String.class
        );
    }

    public Provider getProvider() {
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

    private HttpEntity<Void> createBearerRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }

    private HttpHeaders createAdminHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey);
        return headers;
    }
}
