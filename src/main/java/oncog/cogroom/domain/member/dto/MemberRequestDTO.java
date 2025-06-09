package oncog.cogroom.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDTO {

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MemberInfoUpdateDTO {
        @Email
        private String email;

        @NotBlank
        private String nickname;

        private String imageUrl;

        private String description;

        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$\n")
        private String phoneNumber;

        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&]).{8,16}$")
        private String password;
    }

    @Getter
    @Builder
    public static class ExistNicknameDTO {
        @NotBlank
        private String nickname;
    }
}
