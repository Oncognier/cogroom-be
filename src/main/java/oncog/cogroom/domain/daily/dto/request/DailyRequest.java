package oncog.cogroom.domain.daily.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class DailyRequest {

    @Getter
    @AllArgsConstructor
    public static class DailyAnswerDTO {
        private Long assignedQuestionId;

        @NotBlank(message = "답변을 1자 이상 작성해주세요.")
        @Size(max = 100, message = "답변은 100자 이하여야 합니다.")
        private String answer;
    }
}
