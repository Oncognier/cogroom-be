package oncog.cogroom.domain.daily.respository;

import oncog.cogroom.domain.daily.entity.Answer;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("""
        SELECT a.answer FROM Answer a
        WHERE a.member.id = :id
        AND a.createdAt BETWEEN :start AND :end
    """)
    Optional<String> findByMemberAndCreatedAtBetween(Long id, LocalDateTime start, LocalDateTime end);

    boolean existsByMemberAndQuestion(Member member, Question question);
}
