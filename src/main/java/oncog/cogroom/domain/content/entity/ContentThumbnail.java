package oncog.cogroom.domain.content.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "CONTENT_THUMBNAIL")
public class ContentThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Content content;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder;

}
