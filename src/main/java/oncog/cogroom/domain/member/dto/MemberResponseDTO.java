package oncog.cogroom.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponseDTO {

    @Builder
    @Getter
    public static class MemberInfoDTO {
        private String email;
        private String nickname;
        private String imgUrl;
        private String description;
        private String phoneNumber;
    }
}
