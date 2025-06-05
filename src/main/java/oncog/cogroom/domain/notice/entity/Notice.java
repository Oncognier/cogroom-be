package oncog.cogroom.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.global.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "NOTICE")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;
}
