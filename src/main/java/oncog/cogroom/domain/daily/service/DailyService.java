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
import oncog.cogroom.domain.daily.respository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final StreakService streakService;

    public DailyQuestionResponseDTO getTodayDailyQuestion() {
        Long memberId = getMemberId();

        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();

        int streakDays = streakService.getStreakDays(memberId);

        AssignedQuestion question = getAssignedQuestion(memberId, startOfToday);

        String answer = question.isAnswered() ? getAnswerIfExists(memberId, startOfToday, endOfToday) : null;

        return DailyQuestionResponseDTO.builder()
                .streakDays(streakDays)
                .questionId(question.getQuestion().getId())
                .question(question.getQuestion().getQuestion())
                .answer(answer)
                .build();

    }

    @Transactional
    public void createDailyAnswer(DailyAnswerRequestDTO request) {
        Long memberId = getMemberId();
        Member member = findMember(memberId);
        Question question = findQuestion(request.getQuestionId());

        checkDuplicateAnswer(member, question); // 중복 답변 확인

        checkIsAssignedQuestion(member, question);

        saveAnswer(member, question, request.getAnswer());
        updateAssignedQuestionStatus(member);

        updateStreakInfo(member);
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

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new DailyException(DailyErrorCode.QUESTION_NOT_FOUND));
    }

    private void saveAnswer(Member member, Question question, String content) {
        Answer answer = Answer.builder()
                .member(member)
                .question(question)
                .answer(content)
                .build();
        answerRepository.save(answer);
    }

    private void updateAssignedQuestionStatus(Member member) {
        LocalDateTime startOfToday = getStartOfToday();
        AssignedQuestion assignedQuestion = getAssignedQuestion(member.getId(), startOfToday);
        assignedQuestion.setIsAnswered();
    }

    private void checkDuplicateAnswer(Member member, Question question) {
        if (answerRepository.existsByMemberAndQuestion(member, question)) {
            throw new DailyException(DailyErrorCode.ALREADY_ANSWERED);
        }
    }

    private void checkIsAssignedQuestion(Member member, Question question) {
        LocalDateTime startOfToday = getStartOfToday();
        AssignedQuestion assignedQuestion = getAssignedQuestion(member.getId(), startOfToday);
        if (!assignedQuestion.getQuestion().getId().equals(question.getId())) {
            throw new DailyException(DailyErrorCode.INVALID_QUESTION);
        }
    }

    private void updateStreakInfo(Member member) {
        Streak streak = streakService.getOrCreateStreak(member);
        streak.updateTotalDays(); // 스트릭 날짜 + 1
        streakService.createStreakLog(member, streak); // streak 로그 생성
    }
}
