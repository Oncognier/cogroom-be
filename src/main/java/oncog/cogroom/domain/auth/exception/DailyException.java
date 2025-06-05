package oncog.cogroom.global.exception.domain;

import lombok.Getter;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.CustomException;

@Getter
public class DailyException extends CustomException {

    public DailyException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
