package oncog.cogroom.domain.member.event;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.daily.entity.Answer;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.repository.AnswerRepository;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.domain.streak.entity.Streak;
import oncog.cogroom.domain.streak.entity.StreakLog;
import oncog.cogroom.domain.streak.repository.StreakLogRepository;
import oncog.cogroom.domain.streak.repository.StreakRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MembersWithdrawalEventListener {

    private final AssignedQuestionRepository assignedQuestionRepository;
    private final AnswerRepository answerRepository;
    private final MemberService memberService;
    private final StreakRepository streakRepository;
    private final StreakLogRepository streakLogRepository;

    @EventListener()
    public void handleWithdrawnMembers(MembersWithDrawnEvent event) {
        Member unknownMember = memberService.getOrCreateUnknownMember();

        // 탈퇴한 사용자가 작성했던 데이터의 작성자 전체 수정
        for (Member member : event.getWithdrawnMembers()) {
            List<AssignedQuestion> questions = assignedQuestionRepository.findByMember(member);
            if (!questions.isEmpty()) {
                questions.forEach( q -> q.setMember(unknownMember));
                assignedQuestionRepository.saveAll(questions);
            }

            List<Answer> answers = answerRepository.findByMember(member);
            if(!answers.isEmpty()){
                answers.forEach(a -> a.setMember(unknownMember));
                answerRepository.saveAll(answers);
            }

            Optional<Streak> streak = streakRepository.findByMember(member);
            streak.ifPresent(st -> {
                st.setMember(unknownMember);
                streakRepository.save(st);
            });

            List<StreakLog> streakLogs = streakLogRepository.findByMember(member);
            if (!streakLogs.isEmpty()) {
                streakLogs.forEach(log -> log.setMember(unknownMember));
                streakLogRepository.saveAll(streakLogs);
            }

        }
    }

}
