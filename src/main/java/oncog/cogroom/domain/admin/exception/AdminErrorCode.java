package oncog.cogroom.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode implements BaseErrorCode {

    // 데일리 질문
    QUESTION_LIST_EMPTY_ERROR("QUESTION_LIST_EMPTY_ERROR", HttpStatus.BAD_REQUEST, "질문을 최소 한 개 이상 입력해주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
