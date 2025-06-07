package oncog.cogroom.domain.daily.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyAnswerRequestDTO {
    @NotBlank
    private String answer;
}
