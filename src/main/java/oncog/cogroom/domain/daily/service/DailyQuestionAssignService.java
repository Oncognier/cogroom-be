package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.daily.exception.DailyException;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyQuestionAssignService {

    private final AssignedQuestionRepository assignedQuestionRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final DailyService dailyService;
    private final Random random = new Random();

    private static final long FIRST_QUESTION_ID = 1; // 회원가입 시 할당받는 최초 질문 id

    // 회원가입 시 질문 할당
    @Transactional
    public void assignDailyQuestionAtSignup(Provider provider, String providerId) {
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        assignFirstDailyQuestion(member);
    }

//     오늘 이미 질문이 할당됐는지 확인 (질문 중복 할당 방지)
    public boolean alreadyAssignedQuestionToday(Member member) {
        LocalDateTime startOfToday = dailyService.getStartOfToday();
        LocalDateTime endOfToday = dailyService.getEndOfToday();
        return assignedQuestionRepository.existsByMemberAndAssignedDateBetween(member, startOfToday, endOfToday);
    }

    // 멤버에게 할당할 질문 레벨 조회
    public QuestionLevel getNextQuestionLevel(Member member) {
        for (QuestionLevel level : QuestionLevel.values()) {
            int count = questionRepository.countUnansweredByMemberAndLevel(member.getId(), level);

            log.info("member: {}, level: {}, 남은 질문 수: {}", member.getId(), level, count);

            if (count > 0) return level;
        }
        return null; // 모든 질문을 다 답한 경우
    }

    private void saveAssignedQuestion(Member member, Question question) {
        AssignedQuestion assignedQuestion = AssignedQuestion.builder()
                .member(member)
                .question(question)
                .isAnswered(false)
                .assignedDate(LocalDateTime.now().toLocalDate().atStartOfDay())
                .build();

        assignedQuestionRepository.save(assignedQuestion);
    }

    private void assignFirstDailyQuestion(Member member) {
        if (alreadyAssignedQuestionToday(member)) {
            log.info("멤버: {} 에게 이미 질문이 할당되었습니다.", member.getId());
            return;
        }

        Question question = questionRepository.findById(FIRST_QUESTION_ID)
                .orElseThrow(() -> new DailyException(DailyErrorCode.FIRST_QUESTION_NOT_FOUND_ERROR));

        saveAssignedQuestion(member, question);
    }
}
