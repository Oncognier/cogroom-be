package oncog.cogroom.domain.daily.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyAnswerRequestDTO {
    private Long questionId;

    @NotBlank
    private String answer;
}
