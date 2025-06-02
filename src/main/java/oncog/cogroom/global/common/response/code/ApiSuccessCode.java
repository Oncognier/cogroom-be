package oncog.cogroom.global.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiSuccessCode {

    SUCCESS("200", HttpStatus.OK, "요청에 성공했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
