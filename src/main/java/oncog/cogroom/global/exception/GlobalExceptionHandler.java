package oncog.cogroom.global.exception;

import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    // 공통 ResponseEntity
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(BaseErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiErrorResponse.of(errorCode));
    }

}
