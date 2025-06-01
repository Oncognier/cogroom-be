package oncog.cogroom.domain.streak.repository;

import oncog.cogroom.domain.streak.entity.StreakLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface StreakLogRepository extends JpaRepository<StreakLog, Long> {
    boolean existsByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
