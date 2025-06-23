package oncog.cogroom.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode implements BaseErrorCode {

    // 데일리
    QUESTION_LIST_EMPTY_ERROR("QUESTION_LIST_EMPTY_ERROR", HttpStatus.BAD_REQUEST, "질문을 최소 한 개 이상 입력해주세요."),
    LEVEL_EMPTY_ERROR("LEVEL_EMPTY_ERROR", HttpStatus.BAD_REQUEST, "난이도를 입력해주세요."),
    INVALID_LEVEL_ERROR("INVALID_LEVEL_ERROR", HttpStatus.BAD_REQUEST, "유효하지 않은 난이도입니다."),
    CATEGORY_EMPTY_ERROR("CATEGORY_EMPTY_ERROR", HttpStatus.BAD_REQUEST, "카테고리를 최소 한 개 이상 입력해주세요."),
    INVALID_CATEGORY_ERROR("INVALID_CATEGORY_ERROR", HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
