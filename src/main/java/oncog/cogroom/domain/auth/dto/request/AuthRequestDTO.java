package oncog.cogroom.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.member.enums.Provider;

public class AuthRequestDTO {

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginRequestDTO {

        // 공통
        private Provider provider;
        private String email;

        // 로컬
        private String password;

        // 소셜
        private String code;
        private String nickname;
        private String providerId;
    }

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignupRequestDTO {

        // 공통
        private Provider provider;
        private String email;
        private String nickname;

        // 로컬
        private String password;

        // 소셜
        private String providerId;
    }
}
