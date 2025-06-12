package oncog.cogroom.domain.content.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentResponseDTO {
    private Long id;
    private String imageUrl;
    private String title;
    private String summary;
}
