package oncog.cogroom.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성, 외부에서 직접 생성 불가
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder
@Table(name = "MEMBER")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider; // KAKAO 등

    @Column
    private String providerId; // 소셜 로그인일 경우만 저장

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String description;

    @Column
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    @Builder.Default
    private MemberRole role = MemberRole.USER; // USER, ADMIN, INSTRUCTOR

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE; // ACTIVE, SUSPENDED, WITHDRAWN


    public void updateMemberInfo(MemberRequestDTO.MemberInfoUpdateDTO request) {
        this.email = request.getEmail();
        this.description = request.getDescription();
        this.phoneNumber = request.getPhoneNumber();
        this.profileImageUrl = request.getImageUrl();
        this.nickname = request.getNickname();
    }
}
