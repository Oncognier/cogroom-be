package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("""
        SELECT COUNT(q) FROM Question q
        WHERE q.level = :level
          AND q.id NOT IN (
            SELECT aq.question.id FROM AssignedQuestion aq
            WHERE aq.member.id = :id
              AND aq.isAnswered = true
          )
    """)
    int countUnansweredByMemberAndLevel(Long id, QuestionLevel level);

    @Query("""
        SELECT q FROM Question q
        WHERE q.level = :level
          AND q.id NOT IN (
            SELECT aq.question.id FROM AssignedQuestion aq
            WHERE aq.member.id = :id
              AND aq.isAnswered = true
          )
    """)
    List<Question> findUnansweredByMemberAndLevel(Long id, QuestionLevel level);
}
