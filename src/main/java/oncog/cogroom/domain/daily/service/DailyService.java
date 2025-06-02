package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponse;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.respository.AnswerRepository;
import oncog.cogroom.domain.daily.respository.AssignedQuestionRepository;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.repository.StreakRepository;
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
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusNanos(1);

        int streakDays = streakRepository.findByMemberId(memberId)
                .map(Streak::getTotalDays)
                .orElse(0); // 존재하지 않을 경우 0으로 반환

        AssignedQuestion question = assignedQuestionRepository
                .findByMemberAndAssignedDate(memberId, startOfToday)
                .orElseThrow(() -> new RuntimeException("오늘 할당된 질문이 없습니다."));

        String answer = question.isAnswered()
                ? getAnswerIfExists(memberId, startOfToday, endOfToday)
                : null;

        return DailyQuestionResponse.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }

    private String getAnswerIfExists(Long memberId, LocalDateTime start, LocalDateTime end) {
        return answerRepository.findByMemberAndCreatedAtBetween(memberId, start, end).orElse(null);
    }
}
