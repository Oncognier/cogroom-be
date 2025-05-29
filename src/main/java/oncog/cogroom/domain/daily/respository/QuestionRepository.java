package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("""
        SELECT COUNT(aq) FROM AssignedQuestion aq
        WHERE aq.member.id = :id
        AND aq.isAnswered = false
        AND aq.question.level = :level
    """)
    int countUnansweredByMemberAndLevel(Long id, QuestionLevel level);

    @Query("""
        SELECT aq.question FROM AssignedQuestion aq
        WHERE aq.member.id = :id
        AND aq.isAnswered = false
        AND aq.question.level = :level
    """)
    List<Question> findUnansweredByMemberAndLevel(Long id, QuestionLevel level);
}
