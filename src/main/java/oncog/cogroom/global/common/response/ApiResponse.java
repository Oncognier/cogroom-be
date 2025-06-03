package oncog.cogroom.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이면 JSON 응답에서 제외
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T result;

    public static <T> ApiResponse<T> of(ApiSuccessCode code, T result) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), result);
    }

    public static <T> ApiResponse<T> of(ApiSuccessCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }
}
