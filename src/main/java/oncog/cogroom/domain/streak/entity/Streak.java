package oncog.cogroom.domain.streak.entity;

import jakarta.persistence.*;
import lombok.*;
import oncog.cogroom.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "STREAK")
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private Member member;

    @Column
    private String shareUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer dailyStreak = 0; // 누적 스트릭 일 수

    public void updateTotalDays() {
        this.dailyStreak += 1;
    }

    public void resetTotalDays() {
        this.dailyStreak = 0;
    }
}
