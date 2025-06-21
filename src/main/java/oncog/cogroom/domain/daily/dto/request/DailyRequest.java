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

        @NotBlank
        @Size(max = 100, message = "answerSizeExceeded")
        private String answer;
    }
}
