package oncog.cogroom.domain.auth.service;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.userinfo.KakaoUserInfo;
import oncog.cogroom.domain.auth.userinfo.SocialUserInfo;
import oncog.cogroom.domain.member.enums.MemberRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = new DefaultOAuth2UserService().loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        SocialUserInfo userInfo = switch (provider) {
            case "kakao" -> new KakaoUserInfo(user.getAttributes());
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };

        Map<String, Object> mapped = Map.of(
                "email", userInfo.getEmail(),
                "nickname", userInfo.getNickname(),
                "provider", userInfo.getProvider(),
                "providerId", userInfo.getProviderId()
        );

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(MemberRole.USER.name())),
                mapped,
                "email"
        );
    }
}
