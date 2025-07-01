package oncog.cogroom.domain.daily.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DailyErrorCode implements BaseErrorCode {

    QUESTION_NOT_FOUND_ERROR("QUESTION_NOT_FOUND_ERROR", HttpStatus.NOT_FOUND, "데일리 질문을 찾을 수 없습니다."),
    FIRST_QUESTION_NOT_FOUND_ERROR("FIRST_QUESTION_NOT_FOUND_ERROR", HttpStatus.NOT_FOUND, "최초 데일리 질문을 찾을 수 없습니다."),
    ASSIGNED_QUESTION_NOT_FOUND_ERROR("ASSIGNED_QUESTION_NOT_FOUND_ERROR", HttpStatus.NOT_FOUND, "해당 id를 가진 할당된 데일리 질문을 찾을 수 없습니다."),
    ANSWER_TIME_EXPIRED_ERROR("ANSWER_TIME_EXPIRED_ERROR", HttpStatus.FORBIDDEN, "오늘 할당된 질문에만 답변을 작성 및 수정할 수 있습니다."),
    ANSWER_NOT_FOUND_ERROR("ANSWER_NOT_FOUND_ERROR", HttpStatus.NOT_FOUND, "데일리 답변을 찾을 수 없습니다."),
    QNA_NOT_FOUND_ERROR("QNA_NOT_FOUND_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "질문 - 답변 데이터를 구성하는 중 문제가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
