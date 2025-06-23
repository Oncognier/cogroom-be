package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.*;
import lombok.*;
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
}
