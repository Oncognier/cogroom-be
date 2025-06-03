package oncog.cogroom.global.exception;

import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;

@Getter
public class CustomException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
