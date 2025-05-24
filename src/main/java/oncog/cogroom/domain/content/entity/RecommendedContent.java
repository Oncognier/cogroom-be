package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "RECOMMENDED_CONTENT")
public class RecommendedContent {

    @Id
    @Column
    private Long id;

    @MapsId // content의 id를 fk이자 pk로 사용
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Content content;

    @Column(nullable = false)
    private Integer displayOrder;

}
