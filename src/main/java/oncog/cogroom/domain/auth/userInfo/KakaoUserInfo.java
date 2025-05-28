package oncog.cogroom.domain.auth.userInfo;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.member.enums.Provider;

@RequiredArgsConstructor
public class KakaoUserInfo implements SocialUserInfo{

    private final SocialResponseDTO.KakaoUserResponseDTO kakaoUserResponse;

    @Override
    public String getProviderId() {
        return kakaoUserResponse.getUserId().toString();
    }

    @Override
    public String getEmail() {
        return kakaoUserResponse.getKakaoAccount().getKakaoEmail();
    }

    @Override
    public String getNickname() {
        return kakaoUserResponse.getProperties().get("nickname");
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }







}
