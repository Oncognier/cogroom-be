package oncog.cogroom.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.ApiErrorCode;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private final String code;
    private final String message;

    public static <T> ApiResponse<T> success(ApiErrorCode code) {
        return new ApiResponse<>(code.name(), code.getMessage(), null);
    }
}
