package oncog.cogroom.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.common.entity.BaseTimeEntity;
import oncog.cogroom.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "NOTICE")
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ImageUrl;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "created_by")
    private Member createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "updated_by")
    private Member updatedBy;
}
