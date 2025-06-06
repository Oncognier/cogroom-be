package oncog.cogroom.global.exception;

import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException e) {
        e.printStackTrace();
        log.error("{} : {}", e.getClass(), e.getMessage());

        return buildErrorResponse(e.getErrorCode());
    }

    // 그 외 exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknownException(Exception e) {
        e.printStackTrace();
        log.error("{} : {}", e.getClass(), e.getMessage());

        return buildErrorResponse(ApiErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 어노테이션 유효성 체크 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleArgumentException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);

        BaseErrorCode baseErrorCode = mapFieldErrorToErrorCode(fieldError);

        return buildErrorResponse(baseErrorCode);
    }

    // 공통 ResponseEntity
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(BaseErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiErrorResponse.of(errorCode));
    }

    private BaseErrorCode mapFieldErrorToErrorCode(FieldError error) {
        String field = error.getField();
        String code = error.getCode();

        if (code.equals("Email")) { // 이메일 형식에 맞지 않는 경우
            return AuthErrorCode.INVALID_EMAIL_FORMAT;
        }
        if (code.equals("Pattern")) { // 비밀번호 포맷이 맞지 않는 경우
            if (field.equals("password")) {
                return AuthErrorCode.INVALID_PASSWORD_FORMAT;
            }
            if (field.equals("phoneNumber")) {
                return AuthErrorCode.INVALID_PHONE_NUMBER_PATTERN;
            }
        }
        if (code.equals("NotBlank") || code.equals("NotNull")) { // 값이 비어있는 경우
            return ApiErrorCode.EMPTY_FILED;
        }

        return ApiErrorCode.BAD_REQUEST;
    }

}
