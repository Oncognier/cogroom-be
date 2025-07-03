package oncog.cogroom.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

public class MemberRequest {

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MemberInfoUpdateDTO {
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 30, message = "이메일은 30자 이내여야 합니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글,영문,숫자만 사용할 수 있습니다.")
        @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
        private String nickname;

        private String imageUrl;

        private String description;

        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 잘못되었습니다.")
        private String phoneNumber;

        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&]).{8,16}$", message = "비밀번호는 영문 소문자, 대문자, 특수 문자로 구성되어야 합니다.")
        private String password;

        public void updateImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    @Getter
    @Builder
    public static class ExistNicknameDTO {
        @NotBlank
        private String nickname;
    }

    @Getter
    @Builder
    public static class WithdrawDTO {

        @NotEmpty
        private String reason;
    }
}
