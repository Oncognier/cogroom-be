package oncog.cogroom.domain.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.respository.AssignedQuestionRepository;
import oncog.cogroom.domain.daily.respository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyQuestionAssignService {

    private final AssignedQuestionRepository assignedQuestionRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final Random random = new Random();

    @Transactional
    public void assignDailyQuestions() {
        List<Member> members = memberRepository.findAll();
        members.forEach(this::assignDailyQuestion); // 많은 양의 insert문이 실행되므로 배치 적용 필요
    }

    // 질문을 아직 할당받지 않은 멤버에게 랜덤 질문 할당
    private void assignDailyQuestion(Member member) {
        if (alreadyAssignedQuestionToday(member)) return;

        QuestionLevel level = getNextQuestionLevel(member);

        List<Question> candidates = (level != null)
                ? questionRepository.findUnansweredByMemberAndLevel(member.getId(), level)
                : questionRepository.findAll();

        Question question = candidates.get(random.nextInt(candidates.size()));

        if (candidates.isEmpty()) {
            System.out.println("No question candidates found for " + member);
            return;
        }

        saveAssignedQuestion(member, question);
    }

//     오늘 이미 질문이 할당됐는지 확인 (오류로 인한 스케쥴러 중복 실행 방지)
    private boolean alreadyAssignedQuestionToday(Member member) {
        return assignedQuestionRepository.existsByMemberAndAssignedDateAfter(
                member, LocalDate.now().atStartOfDay()
        );
    }

    // 멤버에게 할당할 질문 레벨 찾기
    private QuestionLevel getNextQuestionLevel(Member member) {
        for (QuestionLevel level : QuestionLevel.values()) {
            int count = questionRepository.countUnansweredByMemberAndLevel(member.getId(), level);
            if (count > 0) return level;
        }
        return null; // 모든 질문을 다 답한 경우
    }

    // 랜덤으로 할당된 질문을 테이블에 저장
    private void saveAssignedQuestion(Member member, Question question) {
        AssignedQuestion assignedQuestion = AssignedQuestion.builder()
                .member(member)
                .question(question)
                .isAnswered(false)
                .assignedDate(LocalDateTime.now().toLocalDate().atStartOfDay())
                .build();

        assignedQuestionRepository.save(assignedQuestion);
    }

}
