package oncog.cogroom.global.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiErrorCode implements BaseErrorCode {

    // 공통
    INVALID_USER_ROLE("INVALID_USER_ROLE", HttpStatus.FORBIDDEN, "유효하지 않은 사용자 권한입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_LOGOUT_USER("ALREADY_LOGOUT_USER", HttpStatus.NOT_FOUND, "이미 로그아웃된 사용자입니다."),
    NOT_FOUND_API("NOT_FOUND_API", HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),

    // 회원가입, 정보 수정
    NEED_VERIFICATION("NEED_VERIFICATION", HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다."),
    DUPLICATE_USER_NICKNAME("DUPLICATE_USER_NICKNAME", HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),


    // 시스템
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
