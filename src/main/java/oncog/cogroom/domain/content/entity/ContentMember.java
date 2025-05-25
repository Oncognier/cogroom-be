package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.global.common.entity.BaseTimeEntity;
import oncog.cogroom.domain.member.entity.Member;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "CONTENT_MEMBER")
public class ContentMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(precision = 5, scale = 2, nullable = false) // 전체 자릿수 5, 소수점 이하 2자리까지 표시
    private BigDecimal progressRate;

    @Column(nullable = false)
    private LocalDateTime expireAt; // NULL 이면 무기한

}
