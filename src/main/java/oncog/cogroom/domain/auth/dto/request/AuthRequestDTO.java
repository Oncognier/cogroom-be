package oncog.cogroom.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.member.enums.Provider;

public class AuthRequestDTO {

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginRequestDTO {

        // 공통
        @NotNull
        private Provider provider;

        @Email
        @Size(max = 30)
        private String email;

        // 로컬
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&]).{8,16}$")
        private String password;

        // 소셜
        private String code;
        private String providerId;
    }

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignupRequestDTO {

        // 공통
        @NotNull
        private Provider provider;

        @NotBlank
        private String nickname;

        @Email
        @Size(max = 30)
        private String email;

        // 로컬
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&]).{8,16}$")
        private String password;

        // 소셜
        private String providerId;
    }

    @Getter
    public static class EmailRequestDTO{

        @NotBlank
        @Email
        private String email;
    }
}
