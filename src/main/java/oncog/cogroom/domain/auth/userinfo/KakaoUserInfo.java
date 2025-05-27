package oncog.cogroom.domain.auth.userinfo;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.enums.Provider;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements SocialUserInfo{
    private final Map<String, Object> attributes;


    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String,Object> account = (Map<String,Object>) attributes.get("kakao_account");
        return account == null ? null : (String) account.get("email");
    }
    @Override
    public String getNickname() {
        Map<String,Object> account = (Map<String,Object>) attributes.get("kakao_account");
        Map<String,Object> profile = account == null ? null : (Map<String,Object>) account.get("profile");
        return profile == null ? null : (String) profile.get("nickname");
    }
}
