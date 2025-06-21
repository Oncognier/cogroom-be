package oncog.cogroom.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

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
    }

    @Builder
    @Getter
    public static class MemberMyPageInfoDTO {
        private String nickname;
        private Long signupDays;
        private Integer dailyStreak;
    }
}
