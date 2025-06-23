package oncog.cogroom.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "CATEGORY")
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "created_by")
    private Member createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "updated_by")
    private Member updatedBy;

}
