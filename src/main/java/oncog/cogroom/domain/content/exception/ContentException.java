package oncog.cogroom.domain.content.exception;

import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.CustomException;

public class ContentException extends CustomException {

    public ContentException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
