package oncog.cogroom.domain.daily.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DailyErrorCode implements BaseErrorCode {

    DAILY_QUESTION_NOT_FOUND("DAILY_QUESTION_NOT_FOUND", HttpStatus.NOT_FOUND, "데일리 질문을 찾을 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
