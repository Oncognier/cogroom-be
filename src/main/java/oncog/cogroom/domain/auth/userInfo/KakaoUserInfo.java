package oncog.cogroom.domain.auth.userInfo;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.dto.response.SocialUserInfo;
import oncog.cogroom.domain.member.enums.Provider;

@RequiredArgsConstructor
public class KakaoUserInfo implements oncog.cogroom.domain.auth.userInfo.SocialUserInfo {

    private final SocialUserInfo.KakaoUserInfoDTO kakaoUserInfo;

    @Override
    public String getProviderId() {
        return kakaoUserInfo.getUserId().toString();
    }

    @Override
    public String getEmail() {
        return kakaoUserInfo.getKakaoAccount().getKakaoEmail();
    }

    @Override
    public String getNickname() {
        return kakaoUserInfo.getProperties().get("nickname");
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }







}
