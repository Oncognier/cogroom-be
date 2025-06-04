package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.global.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "QUESTION")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionLevel level;
}
