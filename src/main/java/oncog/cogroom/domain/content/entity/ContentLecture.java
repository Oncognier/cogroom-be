package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.common.entity.BaseTimeEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(value = {AuditingEntityListener.class})
@Table(name = "CONTENT_LECTURE")
public class ContentLecture extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Content content;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private String fileUrl;

    @Column
    private Integer lectureTime;

}
