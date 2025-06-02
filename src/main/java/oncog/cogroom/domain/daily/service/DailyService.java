package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponse;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.respository.AnswerRepository;
import oncog.cogroom.domain.daily.respository.AssignedQuestionRepository;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyService {

    private final AnswerRepository answerRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final StreakRepository streakRepository;

    public DailyQuestionResponse getTodayDailyQuestion(Long memberId) {
        LocalDateTime today = LocalDate.now().atStartOfDay();

        int streakDays = streakRepository.findByMemberId(memberId)
                .map(Streak::getTotalDays)
                .orElse(0); // 존재하지 않을 경우 0으로 반환

        AssignedQuestion question = assignedQuestionRepository
                .findByMemberAndAssignedDate(memberId, today)
                .orElseThrow(() -> new RuntimeException("오늘 할당된 질문이 없습니다."));

        String answer = null;

        if (question.isAnswered()) { // 답변이 이미 존재하는 경우
            LocalDateTime endOfToday = today.plusDays(1).minusNanos(1);
            answer = answerRepository.findByMemberAndCreatedAtBetween(memberId, today, endOfToday).orElse(null);
        }

        return DailyQuestionResponse.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }
}
