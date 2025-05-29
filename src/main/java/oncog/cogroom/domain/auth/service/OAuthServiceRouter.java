package oncog.cogroom.domain.auth.service;

import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.member.enums.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 해당 라우터를 통해 소셜 로그인의 진입점을 하나로 통일
 */
@Component
public class OAuthServiceRouter {
    private final Map<Provider, AbstractSocialAuthService> serviceMap;

    // provider에 맞는 service 클래스 추가
    public OAuthServiceRouter(List<AbstractSocialAuthService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(AbstractSocialAuthService::getProvider, service -> service));
    }

    public SocialResponseDTO.LoginResponseDTO login(Provider provider, String authorizationCode) {
        AbstractSocialAuthService service = serviceMap.get(provider);

        if (service == null) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        return service.login(authorizationCode);

    }
}
