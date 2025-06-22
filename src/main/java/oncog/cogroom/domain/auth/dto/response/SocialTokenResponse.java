package oncog.cogroom.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

public class SocialTokenResponse {

    /**
     * KAKAO DTO
     */
    @Getter
    @Builder
    public static class KakaoTokenDTO{

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private String expires;

        @JsonProperty("refresh_token_expires_in")
        private String refreshTokenExpires;
    }

}
