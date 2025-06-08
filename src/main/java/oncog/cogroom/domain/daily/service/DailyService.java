package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.request.DailyAnswerRequestDTO;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.entity.Answer;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.daily.exception.DailyException;
import oncog.cogroom.domain.daily.respository.AnswerRepository;
import oncog.cogroom.domain.daily.respository.AssignedQuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyService extends BaseService {

    private final AnswerRepository answerRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final StreakService streakService;

    public DailyQuestionResponseDTO getTodayDailyQuestion() {
        Member member = getMember();

        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        int streakDays = streakService.getStreakDays(member);

        AssignedQuestion question = getAssignedQuestion(member, startOfToday);

        String answer = question.isAnswered()
                ? getAnswer(member, startOfToday, endOfToday).getAnswer()
                : null;

        return DailyQuestionResponseDTO.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }

    @Transactional
    public void createDailyAnswer(DailyAnswerRequestDTO request) {
        Member member = getMember();

        LocalDateTime startOfToday = getStartOfToday();
        Question question = getAssignedQuestion(member, startOfToday).getQuestion();

        checkDuplicateAnswer(member, question); // 중복 답변 확인

        saveAnswer(member, question, request.getAnswer());
        markAssignedQuestionAsAnswered(member);

        updateStreakInfo(member);
    }

    @Transactional
    public void updateDailyAnswer(DailyAnswerRequestDTO request) {
        Member member = getMember();

        updateAnswer(member, request.getAnswer());
    }

    private AssignedQuestion getAssignedQuestion(Member member, LocalDateTime date) {
        return assignedQuestionRepository.findByMemberAndAssignedDate(member, date)
                .orElseThrow(() -> new DailyException(DailyErrorCode.DAILY_QUESTION_NOT_FOUND));
    }

    private Answer getAnswer(Member member, LocalDateTime start, LocalDateTime end) {
        return answerRepository.findByMemberAndCreatedAtBetween(member, start, end)
                .orElseThrow(() -> new DailyException(DailyErrorCode.ANSWER_NOT_FOUND));
    }

    private LocalDateTime getStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime getEndOfToday() {
        return getStartOfToday().plusDays(1).minusNanos(1);
    }

    private void saveAnswer(Member member, Question question, String content) {
        Answer answer = Answer.builder()
                .member(member)
                .question(question)
                .answer(content)
                .build();
        answerRepository.save(answer);
    }

    private void markAssignedQuestionAsAnswered(Member member) {
        LocalDateTime startOfToday = getStartOfToday();
        AssignedQuestion assignedQuestion = getAssignedQuestion(member, startOfToday);
        assignedQuestion.setIsAnswered();
    }

    private void checkDuplicateAnswer(Member member, Question question) {
        if (answerRepository.existsByMemberAndQuestion(member, question)) {
            throw new DailyException(DailyErrorCode.ALREADY_ANSWERED);
        }
    }

    private void updateStreakInfo(Member member) {
        Streak streak = streakService.getOrCreateStreak(member);
        streak.updateTotalDays(); // 스트릭 날짜 + 1
        streakService.createStreakLog(member, streak); // streak 로그 생성
    }

    private void updateAnswer(Member member, String newAnswer) {
        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        Answer answer = getAnswer(member, startOfToday, endOfToday);
        answer.updateAnswer(newAnswer);
    }

}
