package oncog.cogroom.domain.auth.userInfo;

import oncog.cogroom.domain.member.enums.Provider;

public interface SocialUserInfo {
    String getProviderId();

    String getEmail();

    String getNickname();

    Provider getProvider();
}
