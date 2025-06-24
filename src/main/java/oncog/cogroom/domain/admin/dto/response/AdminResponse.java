package oncog.cogroom.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.daily.entity.Question;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AdminResponse {

    @Getter
    @Builder
    public static class MemberListDTO {

        private String nickname;

        private String email;

        private String imageUrl;

        @JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        private LocalDate createdAt;

        private MemberRole memberRole;


        public static List<AdminResponse.MemberListDTO> of(List<Member> members) {
            return members.stream()
                    .map(member -> AdminResponse.MemberListDTO.builder()
                            .nickname(member.getNickname())
                            .email(member.getEmail())
                            .imageUrl(member.getProfileImageUrl())
                            .memberRole(member.getRole())
                            .createdAt(member.getCreatedAt().toLocalDate())
                            .build()).toList();
        }

    }

    @Getter
    @Builder
    public static class DailyQuestionsDTO {
        private Long questionId;
        private String question;
        private List<String> categories;
        private String level;

        public static DailyQuestionsDTO of(Question question, List<String> categories) {
            return DailyQuestionsDTO.builder()
                    .questionId(question.getId())
                    .question(question.getQuestion())
                    .categories(categories)
                    .level(question.getLevel().name())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class MemberDailyDTO {
        private final Long assignedQuestionId;
        private final String nickname;
        private final String question;
        private final QuestionLevel level;
        private final String category;
        @JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        private final LocalDateTime answeredAt;

    }

    @Getter
    @Builder
    public static class MemberDailyListDTO {
        private final Long assignedQuestionId;
        private final String nickname;
        private final String question;
        private final QuestionLevel level;
        private final Set<String> categories;
        @JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        private final LocalDateTime answeredAt;

    }
    }

