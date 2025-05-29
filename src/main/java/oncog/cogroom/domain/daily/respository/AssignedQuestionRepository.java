package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AssignedQuestionRepository extends JpaRepository<AssignedQuestion, Long> {
    boolean existsByMemberAndAssignedDateAfter(Member member, LocalDateTime assignedDateAfter);
}
