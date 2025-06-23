package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QuestionCategoryId implements Serializable {

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Integer categoryId;
}
