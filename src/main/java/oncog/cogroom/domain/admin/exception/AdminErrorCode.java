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
    LEVEL_INVALID_ERROR("LEVEL_INVALID_ERROR", HttpStatus.BAD_REQUEST, "유효하지 않은 난이도입니다."),
    CATEGORY_EMPTY_ERROR("CATEGORY_EMPTY_ERROR", HttpStatus.BAD_REQUEST, "카테고리를 최소 한 개 이상 입력해주세요."),
    CATEGORY_INVALID_ERROR("CATEGORY_INVALID_ERROR", HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다."),

    // 페이징
    PAGE_OUT_OF_RANGE_ERROR("PAGE_OUT_OF_RANGE_ERROR", HttpStatus.BAD_REQUEST, "요청한 페이지가 범위를 초과했습니다."),;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
