package oncog.cogroom.global.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiErrorCode implements BaseErrorCode {

    // 시스템
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    EMPTY_FILED("EMPTY_FILED", HttpStatus.BAD_REQUEST, "요청 값이 비어있습니다."),
    INVALID_PATTERN("INVALID_PATTERN", HttpStatus.BAD_REQUEST, "입력 값의 패턴이 잘못되었습니다."),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
