package oncog.cogroom.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDTO {

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MemberInfoUpdateDTO {
        private String email;
        private String nickname;
        private String imgUrl;
        private String description;
        private String phoneNumber;
    }
}
