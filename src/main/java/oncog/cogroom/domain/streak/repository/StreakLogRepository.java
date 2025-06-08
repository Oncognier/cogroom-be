package oncog.cogroom.domain.streak.repository;

import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.StreakLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StreakLogRepository extends JpaRepository<StreakLog, Long> {
    boolean existsByMemberAndCreatedAtBetween(Member member, LocalDateTime start, LocalDateTime end);

    List<StreakLog> findAllByMemberAndCreatedAtBetween(Member member, LocalDateTime start, LocalDateTime end);
}
