package oncog.cogroom.domain.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 유효성 검사
    INVALID_EMAIL_FORMAT("INVALID_EMAIL_FORMAT", HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    INVALID_PHONE_NUMBER_PATTERN("INVALID_PATTERN", HttpStatus.BAD_REQUEST, "형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT("INVALID_PASSWORD_FORMAT", HttpStatus.BAD_REQUEST, "비밀번호 형식이 잘못되었습니다."),

    // 카카오 로그인
    KAKAO_INVALID_AUTHORIZATION_CODE("KAKAO_INVALID_AUTHORIZATION_CODE", HttpStatus.BAD_REQUEST, "유효하지 않은 Authorization Code입니다."),
    KAKAO_AUTH_FAILED("KAKAO_AUTH_FAILED", HttpStatus.UNAUTHORIZED, "카카오 인증 요청이 실패했습니다."),

    // JWT 토큰
    INVALID_TOKEN("INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    ALREADY_BLACK_LIST("ALREADY_BLACK_LIST", HttpStatus.UNAUTHORIZED, "이미 블랙리스트에 포함된 토큰입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 이메일
    ALREADY_EXIST_EMAIL("ALREADY_EXIST_EMAIL", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    NOT_VERIFIED_EMAIL("NOT_VERIFIED_EMAIL", HttpStatus.BAD_REQUEST, "인증된 이메일이 아닙니다."),
    EXPIRED_LINK("EXPIRED_LINK", HttpStatus.BAD_REQUEST, "인증 링크 시간이 만료되었습니다."),


    ;


    private final String code;
    private final HttpStatus status;
    private final String message;
}
