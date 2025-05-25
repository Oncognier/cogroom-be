package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.global.common.entity.BaseTimeEntity;
import oncog.cogroom.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "CONTENT_REVIEW")
public class ContentReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private String comment;

}
