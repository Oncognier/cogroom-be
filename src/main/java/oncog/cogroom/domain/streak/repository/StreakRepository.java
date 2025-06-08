package oncog.cogroom.domain.streak.repository;

import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    Optional<Streak> findByMember(Member member);
}
