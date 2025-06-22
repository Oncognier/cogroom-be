package oncog.cogroom.domain.content.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

public class ContentResponse {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HomeContentDTO {
        private Long id;
        private String imageUrl;
        private String title;
        private String summary;
    }
}
