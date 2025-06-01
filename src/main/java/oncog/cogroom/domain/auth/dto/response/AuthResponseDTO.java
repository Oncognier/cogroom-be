package oncog.cogroom.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;

public class AuthResponseDTO {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginResponseDTO{

        SocialUserInfo socialUserInfo;
        ServiceTokenDTO tokens;
        boolean needSignup;

        // accessToken만 반환하는 형식으로 변형
        public LoginResponseDTO getResponseExcludedRefreshToken() {
            ServiceTokenDTO tokens = ServiceTokenDTO.builder()
                    .refreshToken(null)
                    .accessToken(this.getTokens().getAccessToken())
                    .build();

            return LoginResponseDTO.builder()
                    .tokens(tokens)
                    .needSignup(this.needSignup)
                    .socialUserInfo(this.getSocialUserInfo())
                    .build();
        }
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignupResponseDTO{

        ServiceTokenDTO tokens;

        // accessToken만 반환하는 형식으로 변형
        public SignupResponseDTO getResponseExcludedRefreshToken() {
            ServiceTokenDTO tokens = ServiceTokenDTO.builder()
                    .refreshToken(null)
                    .accessToken(this.getTokens().getAccessToken())
                    .build();

            return SignupResponseDTO.builder()
                    .tokens(tokens)
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
