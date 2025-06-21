package oncog.cogroom.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.Map;

public class SocialUserInfo {

    @Getter
    @Builder
    public static class KakaoUserInfoDTO {

        @JsonProperty("id")
        private Long userId;

        //서비스에 연결 완료된 시각. UTC
        @JsonProperty("connected_at")
        private Date connectedAt;

        //카카오싱크 간편가입을 통해 로그인한 시각. UTC
        @JsonProperty("synched_at")
        private Date synchedAt;

        //사용자 프로퍼티
        @JsonProperty("properties")
        private Map<String, String> properties;

        //사용자 이메일 정보
        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @Getter
        @Builder
        public static class KakaoAccount {

            @JsonProperty("email")
            private String kakaoEmail;

            @JsonProperty("profile")
            private Profile profile;

            @Getter
            @Builder
            public static class Profile {
                @JsonProperty("nickname")
                private String nickname;

                @JsonProperty("profile_image_url")
                private String profileImageUrl;

                @JsonProperty("thumbnail_image_url")
                private String thumbnailImageUrl;
            }
        }
    }
}


