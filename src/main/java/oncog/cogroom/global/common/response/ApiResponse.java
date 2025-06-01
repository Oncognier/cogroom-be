package oncog.cogroom.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이면 JSON 응답에서 제외
public class ApiResponse<T> {
    private final Integer code;
    private final String message;
    private final T result;

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "요청에 성공했습니다.", null);
    }

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(200, "요청에 성공했습니다.", result);
    }
}
