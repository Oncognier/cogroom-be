package oncog.cogroom.domain.daily.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyAnswerRequestDTO {
    private Long assignedQuestionId;

    @NotBlank
    @Size(max = 100, message = "answerSizeExceeded")
    private String answer;
}
