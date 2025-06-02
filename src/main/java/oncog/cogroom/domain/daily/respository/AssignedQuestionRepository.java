package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AssignedQuestionRepository extends JpaRepository<AssignedQuestion, Long> {
    boolean existsByMemberAndAssignedDateAfter(Member member, LocalDateTime assignedDateAfter);

    @Query("""
        SELECT aq FROM AssignedQuestion aq
        WHERE aq.member.id = :id AND aq.assignedDate = :assignedDate
    """)
    Optional<AssignedQuestion> findByMemberAndAssignedDate(Long id, LocalDateTime assignedDate);
}
