package oncog.cogroom.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private final Integer code;
    private final String message;

    public static ApiErrorResponse badRequest(String detail) {
        return new ApiErrorResponse(400, "잘못된 요청입니다.");
    }

    public static ApiErrorResponse unauthorized(String detail) {
        return new ApiErrorResponse(401, "인증이 필요합니다.");
    }

    public static ApiErrorResponse forbidden(String detail) {
        return new ApiErrorResponse(403, "접근이 거부되었습니다.");
    }

    public static ApiErrorResponse serverError(String detail) {
        return new ApiErrorResponse(500, "서버 오류가 발생했습니다.");
    }
}
