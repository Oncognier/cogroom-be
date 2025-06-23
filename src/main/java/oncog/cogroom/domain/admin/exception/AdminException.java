package oncog.cogroom.domain.admin.exception;

import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.CustomException;

public class AdminException extends CustomException {

    public AdminException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
