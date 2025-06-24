package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.global.common.entity.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "QUESTION_CATEGORY")
public class QuestionCategory extends BaseEntity {

    @EmbeddedId
    private QuestionCategoryId id;

    // 조회 목적으로 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    private Category category;
}
