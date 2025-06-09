package oncog.cogroom.domain.streak.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreakCalendarResponseDTO {
    private List<String> streakDateList;
}
