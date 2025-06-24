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

<<<<<<< HEAD
    // 조회 목적으로 사용
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

>>>>>>> 9d06872 (feat: 회원 관리 내부 데일리 콘텐츠 조회 API 구현)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
}
