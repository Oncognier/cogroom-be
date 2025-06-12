package oncog.cogroom.domain.content.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ContentErrorCode implements BaseErrorCode {

    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND", HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    CONTENTS_NOT_FOUND("CONTENTS_NOT_FOUND", HttpStatus.NOT_FOUND, "콘텐츠 목록을 찾을 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
