package oncog.cogroom.global.exception;

import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
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
        String message = error.getDefaultMessage();

        // 형식 관련 오류
        if ("Email".equals(code)) {
            return AuthErrorCode.INVALID_EMAIL_FORMAT;
        }

        if ("Pattern".equals(code)) {
            switch (field) {
                case "password" -> {
                    return  AuthErrorCode.INVALID_PASSWORD_FORMAT;
                }
                case "phoneNumber" -> {
                    return AuthErrorCode.INVALID_PHONE_NUMBER_PATTERN;
                }
                default -> {
                    return ApiErrorCode.INVALID_PATTERN;
                }
            }
        }

        // 필수 값 누락
        if ("NotBlank".equals(code) || "NotNull".equals(code)) {
            return ApiErrorCode.EMPTY_FILED;
        }

        // 글자 수 제한 초과
        if ("Size".equals(code) && "answer".equals(field) && "answerSizeExceeded".equals(message)) {
            return DailyErrorCode.ANSWER_LENGTH_EXCEEDED;
        }

        // 기타 처리되지 않은 예외
        return ApiErrorCode.BAD_REQUEST;
    }


}
