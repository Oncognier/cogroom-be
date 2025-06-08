package oncog.cogroom.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponseDTO {

    @Builder
    @Getter
    public static class MemberInfoDTO {
        private String email;
        private String nickname;
        private String imageUrl;
        private String description;
        private String phoneNumber;
    }

    @Builder
    @Getter
    public static class MemberSummaryDTO {
        private String nickname;
        private String imageUrl;
    }


}
