package oncog.cogroom.global.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiErrorCode implements BaseErrorCode {

    // 시스템
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    EMPTY_FIELD_ERROR("EMPTY_FIELD_ERROR", HttpStatus.BAD_REQUEST, "요청 필드가 비어있습니다."),
    BAD_REQUEST_ERROR("BAD_REQUEST_ERROR", HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
