package oncog.cogroom.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;

public class AuthResponseDTO {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginResultDTO {

        SocialUserInfo socialUserInfo;
        ServiceTokenDTO tokens;
        boolean needSignup;

        // token null로 변경
        public LoginResultDTO excludeTokens() {

            return LoginResultDTO.builder()
                    .tokens(null)
                    .needSignup(this.needSignup)
                    .socialUserInfo(this.getSocialUserInfo())
                    .build();
        }
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignupResultDTO {

        ServiceTokenDTO tokens;

        // token null로 변경
        public SignupResultDTO excludeTokens() {

            return SignupResultDTO.builder()
                    .tokens(null)
                    .build();
        }
    }


    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ServiceTokenDTO {
        private String accessToken;
        private String refreshToken;
    }
}
