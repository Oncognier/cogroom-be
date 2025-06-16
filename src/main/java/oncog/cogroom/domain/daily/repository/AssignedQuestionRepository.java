package oncog.cogroom.domain.daily.repository;

import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignedQuestionRepository extends JpaRepository<AssignedQuestion, Long> {
    boolean existsByMemberAndAssignedDateBetween(Member member, LocalDateTime start, LocalDateTime end);

    Optional<AssignedQuestion> findByMemberAndAssignedDateGreaterThanEqualAndAssignedDateLessThan(Member member, LocalDateTime start, LocalDateTime end);

    Optional<AssignedQuestion> findByMemberAndId(Member member, Long id);

    // 내부 클래스 조회 시 $기호 사용
    @Query("""
        SELECT new oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO$AssignedQuestionWithAnswerDTO( 
            q.question,
            a.answer,
            aq.assignedDate
        ) FROM AssignedQuestion aq
        JOIN Question q ON aq.question.id = q.id AND aq.isAnswered = true
        JOIN Answer a ON a.member.id = :id AND a.question.id = q.id
        WHERE aq.member.id = :id
        ORDER BY aq.assignedDate DESC
    """)
    Optional<List<DailyQuestionResponseDTO.AssignedQuestionWithAnswerDTO>> findAssignedQuestionsWithAnswerByMember(Long id);
}
