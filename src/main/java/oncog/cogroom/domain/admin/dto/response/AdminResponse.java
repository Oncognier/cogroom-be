package oncog.cogroom.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;

import java.time.LocalDate;
import java.util.List;

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
}
