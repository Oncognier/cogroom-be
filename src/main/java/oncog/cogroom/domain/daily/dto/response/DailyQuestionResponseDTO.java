package oncog.cogroom.domain.daily.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyQuestionResponseDTO {
    private int streakDays;
    private Long questionId;
    private Long assignedQuestionId;
    private String question;
    private String answer; // 질문 답변한 경우에만 반환
}
