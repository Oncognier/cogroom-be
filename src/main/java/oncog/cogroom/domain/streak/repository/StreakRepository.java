package oncog.cogroom.domain.streak.repository;

import oncog.cogroom.domain.streak.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakRepository extends JpaRepository<Streak, Long> {
}
