package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.global.common.entity.BaseTimeEntity;
import oncog.cogroom.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ANSWER")
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private String answer;

    public void updateAnswer(String newAnswer) {
        this.answer = newAnswer;
    }
}
