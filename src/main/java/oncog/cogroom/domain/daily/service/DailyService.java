package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.dto.request.DailyAnswerRequestDTO;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.dto.response.HasAnsweredResponseDTO;
import oncog.cogroom.domain.daily.entity.Answer;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.daily.exception.DailyException;
import oncog.cogroom.domain.daily.repository.AnswerRepository;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        AssignedQuestion question = getTodayAssignedQuestion(member);

        String answer = question.isAnswered()
                ? getDailyAnswer(member, startOfToday, endOfToday).getAnswer()
                : null;

        return DailyQuestionResponseDTO.builder()
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
        markAssignedQuestionAsAnswered(member); // 할당 질문의 isAnswered 상태 변경

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

    // 마이 페이지에서 데일리 질문 & 답변 조회
    @Transactional(readOnly = true)
    public List<DailyQuestionResponseDTO.AssignedQuestionWithAnswerDTO> getAssignedAndAnsweredQuestion() {

        List<DailyQuestionResponseDTO.AssignedQuestionWithAnswerDTO> response =
                assignedQuestionRepository.findAssignedQuestionsWithAnswerByMember(jwtProvider.extractMemberId())
                .orElseThrow(() -> new DailyException(DailyErrorCode.ANSWER_NOT_FOUND));

        // 데일리 질문이 할당된 시간이 00:00:00 ~ 23:59:59 내로 할당되었는지 검사
        response.forEach(res -> {
            LocalDateTime assignedDate = res.getAssignedDate();
            if (getStartOfToday().isBefore(assignedDate) && getEndOfToday().isAfter(assignedDate)) {
                res.setUpdatable(true);
            }
        });

        return response;
    }


    // 데일리 최초 답변 여부 조회
    public HasAnsweredResponseDTO getHasAnswered() {
        Member member = getMember();

        boolean hasAnswered = answerRepository.existsByMember(member);

        return HasAnsweredResponseDTO.builder()
                .hasAnswered(hasAnswered)
                .build();
    }

    private AssignedQuestion getTodayAssignedQuestion(Member member) {
        LocalDateTime startOfToday = getStartOfToday();
        LocalDateTime endOfToday = getEndOfToday();
        return assignedQuestionRepository.findByMemberAndAssignedDateGreaterThanEqualAndAssignedDateLessThan(member, startOfToday, endOfToday)
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

    // 금일 시작 시간 조회 (00:00:00)
    public LocalDateTime getStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    // 금일 마지막 시간 조회 (23:59:59)
    public LocalDateTime getEndOfToday() {
        return getStartOfToday().plusDays(1);
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

    // now, target이 같은 날짜, 시간인지 확인
    private void checkIsSameDay(LocalDateTime now, LocalDateTime target) {
        log.info("now: {}, target: {}", now, target);
        if (!now.toLocalDate().isEqual(target.toLocalDate())) {
            log.info("날짜가 일치하지 않습니다.");
            throw new DailyException(DailyErrorCode.ANSWER_TIME_EXPIRED);
        }
    }

}
