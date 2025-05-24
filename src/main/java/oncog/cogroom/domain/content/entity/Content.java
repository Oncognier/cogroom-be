package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.common.entity.BaseTimeEntity;
import oncog.cogroom.common.entity.Category;
import oncog.cogroom.domain.content.enums.ContentLevel;
import oncog.cogroom.domain.content.enums.ContentStatus;
import oncog.cogroom.domain.content.enums.ContentType;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(value = {AuditingEntityListener.class})
@Table(name = "CONTENT")
public class Content extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentLevel level;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type; // 강의(LECTURE), PDF

    @Column(nullable = false)
    private Integer learningPeriod; // 0이면 무기한

    @Column(nullable = false)
    private String instructorImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status;

    @Column(nullable = false)
    private Integer totalLectureTime; // 초 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "created_by")
    private Member createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "updated_by")
    private Member updatedBy;

}
