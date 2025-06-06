package oncog.cogroom.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

        private String imgUrl;

        @NotBlank
        private String description;

        @NotBlank
        private String phoneNumber;
    }

    @Getter
    @Builder
    public static class ExistNicknameDTO {
        @NotBlank
        private String nickname;
    }
}
