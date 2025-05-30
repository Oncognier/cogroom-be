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
        }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignupResponseDTO{

        ServiceTokenDTO tokens;
    }


    @Builder
    @Getter
    public static class ServiceTokenDTO {
        private String accessToken;
        private String refreshToken;
    }
}
