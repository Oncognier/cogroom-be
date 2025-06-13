package oncog.cogroom.domain.daily.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HasAnsweredResponseDTO {
    private boolean hasAnswered;
}
