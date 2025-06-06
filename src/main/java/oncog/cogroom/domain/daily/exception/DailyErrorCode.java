package oncog.cogroom.domain.daily.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DailyErrorCode implements BaseErrorCode {

    DAILY_QUESTION_NOT_FOUND("DAILY_QUESTION_NOT_FOUND", HttpStatus.NOT_FOUND, "데일리 질문을 찾을 수 없습니다."),
    QUESTION_NOT_FOUND("QUESTION_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 질문을 찾을 수 없습니다."),
    INVALID_QUESTION("INVALID_QUESTION", HttpStatus.BAD_REQUEST, "오늘 할당된 데일리 질문이 아닙니다."),
    ALREADY_ANSWERED("ALREADY_ANSWERED", HttpStatus.CONFLICT, "이미 답변이 존재합니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
