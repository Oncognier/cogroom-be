package oncog.cogroom.domain.auth.service;

import oncog.cogroom.domain.member.enums.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.*;

/**
 * 해당 라우터를 통해 로그인의 진입점을 하나로 통일
 */
@Component
public class AuthServiceRouter {
    private final Map<Provider, AuthService> serviceMap;

    // provider에 맞는 service 클래스 추가
    public AuthServiceRouter(List<AuthService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(AuthService::getProvider, service -> service));
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        AuthService service = serviceMap.get(request.getProvider());

        if (service == null) {
            throw new IllegalArgumentException("Unsupported provider: " + request.getProvider());
        }

        return service.login(request);

    }

    public SignupResponseDTO signup(SignupRequestDTO request) {
        AuthService service = serviceMap.get(request.getProvider());

        return service.signup(request);
    }
}
