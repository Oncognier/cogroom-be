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
    public static class SignupDTO {

        // 공통
        @NotNull
        private Provider provider;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글,영문,숫자만 사용할 수 있습니다.")
        @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
        private String nickname;

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        @Size(max = 30, message = "이메일은 30자 이내여야 합니다.")
        private String email;

        // 로컬
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&]).{8,16}$", message = "비밀번호는 영문 소문자, 대문자, 특수 문자로 구성되어야 합니다.")
        private String password;

        // 소셜
        private String providerId;
    }

    @Getter
    public static class EmailDTO {

        @NotBlank
        @Email
        private String email;
    }
}
