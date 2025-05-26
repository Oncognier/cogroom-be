package oncog.cogroom.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

public class SocialResponseDTO {



    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginResponseDTO{
        private String email;
        private String nickname;
        private ServiceTokenDTO tokens;
        private boolean needSignup;
    }

    @Builder
    @Getter
    public static class ServiceTokenDTO {
        private String accessToken;
        private String refreshToken;
    }
}


