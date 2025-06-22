package oncog.cogroom.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@Builder
@JsonPropertyOrder({"totalPages", "totalElements", "currentPage", "pageSize", "last", "data"})
public class PageResponse<T>  {
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
    private boolean isLast;
    private List<T> data;

    public static <P,T> PageResponse<T> of(Page<P> page, List<T> data) {
        return PageResponse.<T>builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .isLast(page.isLast())
                .data(data)
                .build();
    }


}
