package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.daily.respository.AnswerRepository;
import oncog.cogroom.domain.daily.respository.AssignedQuestionRepository;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.exception.domain.DailyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyService extends BaseService {

    private final AnswerRepository answerRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final StreakRepository streakRepository;

    public DailyQuestionResponseDTO getTodayDailyQuestion() {
        Long memberId = getMemberId();

        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        int streakDays = getStreakDays(memberId);

        AssignedQuestion question = getAssignedQuestion(memberId, startOfToday);

        String answer = question.isAnswered() ? getAnswerIfExists(memberId, startOfToday, endOfToday) : null;

        return DailyQuestionResponseDTO.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }

    private int getStreakDays(Long memberId) {
        return streakRepository.findByMemberId(memberId)
                .map(Streak::getTotalDays)
                .orElse(0);
    }

    private AssignedQuestion getAssignedQuestion(Long memberId, LocalDateTime date) {
        return assignedQuestionRepository.findByMemberAndAssignedDate(memberId, date)
                .orElseThrow(() -> new DailyException(DailyErrorCode.DAILY_QUESTION_NOT_FOUND));
    }

    private String getAnswerIfExists(Long memberId, LocalDateTime start, LocalDateTime end) {
        return answerRepository.findByMemberAndCreatedAtBetween(memberId, start, end).orElse(null);
    }

    private LocalDateTime getStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime getEndOfToday() {
        return getStartOfToday().plusDays(1).minusNanos(1);
    }
}
