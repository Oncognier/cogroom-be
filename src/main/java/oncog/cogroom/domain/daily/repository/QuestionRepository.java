package oncog.cogroom.domain.daily.repository;

import io.lettuce.core.dynamic.annotation.Param;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
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

    @Query(
            "SELECT DISTINCT q FROM Question q " +
                    "LEFT JOIN QuestionCategory qc ON q.id = qc.id.questionId " +
                    "WHERE (:keyword IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:levels IS NULL OR q.level IN (:levels)) " +
                    "AND (:categoryIds IS NULL OR qc.id.categoryId IN (:categoryIds))"
    )
    Page<Question> findDailyQuestionsByFilter(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("levels") List<String> levels,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
