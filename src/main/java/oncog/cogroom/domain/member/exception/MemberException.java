package oncog.cogroom.domain.member.exception;

import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.CustomException;

public class MemberException extends CustomException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
