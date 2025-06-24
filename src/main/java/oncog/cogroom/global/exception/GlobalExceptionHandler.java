package oncog.cogroom.global.exception;

import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

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
        return buildErrorResponse(mapFieldErrorToErrorCode(e.getFieldError()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleEnumParseError(MethodArgumentTypeMismatchException e) {
        e.printStackTrace();
        log.error("{} : {}", e.getClass(), e.getMessage());

        return buildErrorResponse(ApiErrorCode.TYPE_MISMATCH_ERROR);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorResponse> handleDateParseError(DateTimeParseException e) {
        e.printStackTrace();
        log.error("{} : {}", e.getClass(), e.getMessage());

        return buildErrorResponse(ApiErrorCode.DATE_INVALID_ERROR);
    }


    // 공통 ResponseEntity
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(BaseErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiErrorResponse.of(errorCode));
    }

    // 어노테이션 유효성 체크용 errorResponse 응답 반환하도록 오버로딩
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(ApiErrorResponse errorResponse) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 어노테이션 유효성 체크 응답 생성
    private ApiErrorResponse mapFieldErrorToErrorCode(FieldError error) {
        String message =  error.getDefaultMessage();

        return ApiErrorResponse.of(generateErrorCode(error), message);
    }

    private String generateErrorCode(FieldError error) {
        String field = error.getField();
        String code = error.getCode();
        return (field + "_" + code + "_" + "error").toUpperCase();
    }


}
