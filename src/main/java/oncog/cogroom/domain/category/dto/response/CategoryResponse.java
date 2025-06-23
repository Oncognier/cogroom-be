package oncog.cogroom.domain.category.dto.response;

import lombok.Builder;
import lombok.Getter;

public class CategoryResponse {

    @Getter
    @Builder
    public static class CategoryDTO {
        private Integer id;
        private String name;
    }
}
