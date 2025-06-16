package oncog.cogroom.domain.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 유효성 검사
    EMAIL_INVALID_FORMAT_ERROR("EMAIL_INVALID_FORMAT_ERROR", HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    PHONENUMBER_PATTERN_ERROR("PHONENUMBER_PATTERN_ERROR", HttpStatus.BAD_REQUEST, "전화번호 형식이 올바르지 않습니다."),
    PASSWORD_PATTERN_ERROR("PASSWORD_PATTERN_ERROR", HttpStatus.BAD_REQUEST, "비밀번호 형식이 잘못되었습니다."),

    // 카카오 로그인
    KAKAO_REQUEST_ERROR("KAKAO_REQUEST_ERROR", HttpStatus.UNAUTHORIZED, "카카오 인증 요청이 실패했습니다."),

    // JWT 토큰
    TOKEN_INVALID_ERROR("TOKEN_INVALID_ERROR", HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    TOKEN_BLACK_LIST_ERROR("TOKEN_BLACK_LIST_ERROR", HttpStatus.UNAUTHORIZED, "블랙리스트에 포함된 토큰입니다."),
    TOKEN_EXPIRED_ERROR("TOKEN_EXPIRED_ERROR", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 이메일
    EMAIL_DUPLICATE_ERROR("EMAIL_DUPLICATE_ERROR", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    EMAIL_VERIFICATION_ERROR("EMAIL_VERIFICATION_ERROR", HttpStatus.BAD_REQUEST, "인증된 이메일이 아닙니다."),
    LINK_EXPIRED_ERROR("LINK_EXPIRED_ERROR", HttpStatus.BAD_REQUEST, "인증 링크 시간이 만료되었습니다."),
    EMAIL_PATTERN_ERROR("EMAIL_PATTERN_ERROR", HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    ;


    private final String code;
    private final HttpStatus status;
    private final String message;
}
