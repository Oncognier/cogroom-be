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
        log.info("멤버 아이디: {}", member.getId());

        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        log.info("startOfToday: {}", startOfToday);
        log.info("endOfToday: {}", endOfToday);

        int streakDays = streakService.getStreakDays(member);

        AssignedQuestion question = getTodayAssignedQuestion(member);

        String answer = question.isAnswered()
                ? getDailyAnswer(member, startOfToday, endOfToday).getAnswer()
                : null;

        return DailyQuestionResponseDTO.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .assignedQuestionId(question.getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }

    @Transactional
    public void createDailyAnswer(DailyAnswerRequestDTO request) {
        Member member = getMember();

        Question question = getTodayAssignedQuestion(member).getQuestion();

        checkDuplicateAnswer(member, question); // 중복 답변 확인

        AssignedQuestion assignedQuestion = getAssignedQuestion(member, request.getAssignedQuestionId());
        checkIsSameDay(LocalDateTime.now(), assignedQuestion.getAssignedDate()); // 오늘 할당된 데일리 질문이 맞는지 확인

        saveDailyAnswer(member, question, request.getAnswer());
        markAssignedQuestionAsAnswered(member);

        updateStreakInfo(member);
    }

    @Transactional
    public void updateDailyAnswer(DailyAnswerRequestDTO request) {
        Member member = getMember();

        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        AssignedQuestion assignedQuestion = getAssignedQuestion(member, request.getAssignedQuestionId());
        checkIsSameDay(LocalDateTime.now(), assignedQuestion.getAssignedDate());

        Answer answer = getDailyAnswer(member, startOfToday, endOfToday);
        answer.updateAnswer(request.getAnswer());
    }

    private AssignedQuestion getTodayAssignedQuestion(Member member) {
        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();
        return assignedQuestionRepository.findByMemberAndAssignedDateBetween(member, startOfToday, endOfToday)
                .orElseThrow(() -> new DailyException(DailyErrorCode.DAILY_QUESTION_NOT_FOUND));
    }

    private AssignedQuestion getAssignedQuestion(Member member, Long assignedQuestionId) {
        return assignedQuestionRepository.findByMemberAndId(member, assignedQuestionId)
                .orElseThrow(() -> new DailyException(DailyErrorCode.ASSIGNED_QUESTION_NOT_FOUND));
    }

    private Answer getDailyAnswer(Member member, LocalDateTime start, LocalDateTime end) {
        return answerRepository.findByMemberAndCreatedAtBetween(member, start, end)
                .orElseThrow(() -> new DailyException(DailyErrorCode.ANSWER_NOT_FOUND));
    }

    public LocalDateTime getStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    public LocalDateTime getEndOfToday() {
        return getStartOfToday().plusDays(1).minusNanos(1);
    }

    private void saveDailyAnswer(Member member, Question question, String content) {
        Answer answer = Answer.builder()
                .member(member)
                .question(question)
                .answer(content)
                .build();
        answerRepository.save(answer);
    }

    private void markAssignedQuestionAsAnswered(Member member) {
        AssignedQuestion assignedQuestion = getTodayAssignedQuestion(member);
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

    private void checkIsSameDay(LocalDateTime now, LocalDateTime target) {
        log.info("now: {}, target: {}", now, target);
        if (!now.toLocalDate().isEqual(target.toLocalDate())) {
            log.info("날짜가 일치하지 않습니다.");
            throw new DailyException(DailyErrorCode.ANSWER_TIME_EXPIRED);
        };
    }

}
