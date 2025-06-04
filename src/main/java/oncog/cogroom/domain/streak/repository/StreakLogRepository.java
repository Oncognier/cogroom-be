package oncog.cogroom.domain.streak.repository;

import oncog.cogroom.domain.streak.entity.StreakLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StreakLogRepository extends JpaRepository<StreakLog, Long> {
    boolean existsByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);

    List<StreakLog> findAllByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
