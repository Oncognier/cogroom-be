package oncog.cogroom.domain.daily.batch;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.service.DailyQuestionAssignService;
import oncog.cogroom.domain.member.entity.Member;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyQuestionAssignProcessor implements ItemProcessor<Member, AssignedQuestion> {

    private final DailyQuestionAssignService dailyQuestionAssignService;

    @Override
    public AssignedQuestion process(@NonNull Member member) {
        return dailyQuestionAssignService.assignDailyQuestion(member)
                .orElse(null);
    }
}
