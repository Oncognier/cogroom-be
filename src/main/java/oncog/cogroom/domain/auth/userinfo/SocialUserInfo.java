package oncog.cogroom.domain.auth.userinfo;

import oncog.cogroom.domain.member.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();

    String getProviderId();

    String getEmail();

    String getNickname();
}
