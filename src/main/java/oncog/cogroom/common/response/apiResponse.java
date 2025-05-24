package oncog.cogroom.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이면 JSON 응답에서 제외
public class apiResponse<T> {
    private final Integer code;
    private final String message;
    private final T result;

    public static <T> apiResponse<T> success() {
        return new apiResponse<>(200, "요청에 성공했습니다.", null);
    }

    public static <T> apiResponse<T> success(T result) {
        return new apiResponse<>(200, "요청에 성공했습니다.", result);
    }
}
