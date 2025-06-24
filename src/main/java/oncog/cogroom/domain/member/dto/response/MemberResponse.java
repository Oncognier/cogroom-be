package oncog.cogroom.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.member.enums.MemberRole;

public class MemberResponse {

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
        private MemberRole memberRole;

    }

    @Builder
    @Getter
    public static class MemberMyPageInfoDTO {
        private String nickname;
        private Long signupDays;
        private Integer dailyStreak;
    }
}
