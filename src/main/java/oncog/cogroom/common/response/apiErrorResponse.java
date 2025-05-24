package oncog.cogroom.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class apiErrorResponse {
    private final Integer code;
    private final String message;

    public static apiErrorResponse badRequest(String detail) {
        return new apiErrorResponse(400, "잘못된 요청입니다.");
    }

    public static apiErrorResponse unauthorized(String detail) {
        return new apiErrorResponse(401, "인증이 필요합니다.");
    }

    public static apiErrorResponse forbidden(String detail) {
        return new apiErrorResponse(403, "접근이 거부되었습니다.");
    }

    public static apiErrorResponse serverError(String detail) {
        return new apiErrorResponse(500, "서버 오류가 발생했습니다.");
    }
}
