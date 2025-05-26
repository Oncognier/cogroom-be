package oncog.cogroom.domain.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ASSIGNED_QUESTION")
public class AssignedQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Question question;

    @Column(nullable = false)
    private boolean isAnswered = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime assignedDate;

}
