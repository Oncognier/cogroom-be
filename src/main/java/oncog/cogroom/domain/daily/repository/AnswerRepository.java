package oncog.cogroom.domain.daily.repository;

import oncog.cogroom.domain.daily.entity.Answer;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByMemberAndCreatedAtBetween(Member member, LocalDateTime start, LocalDateTime end);

    boolean existsByMemberAndQuestion(Member member, Question question);

    boolean existsByMember(Member member);
}
