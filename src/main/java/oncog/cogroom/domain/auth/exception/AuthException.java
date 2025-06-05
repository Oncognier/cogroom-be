package oncog.cogroom.domain.auth.exception;

import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.CustomException;

@Getter
public class AuthException extends CustomException {

    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
