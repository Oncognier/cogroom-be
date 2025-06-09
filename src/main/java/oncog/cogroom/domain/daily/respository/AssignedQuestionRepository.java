package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AssignedQuestionRepository extends JpaRepository<AssignedQuestion, Long> {
    boolean existsByMemberAndAssignedDateBetween(Member member, LocalDateTime start, LocalDateTime end);

    Optional<AssignedQuestion> findByMemberAndAssignedDateGreaterThanEqualAndAssignedDateLessThan(Member member, LocalDateTime start, LocalDateTime end);


    Optional<AssignedQuestion> findByMemberAndId(Member member, Long id);
}
