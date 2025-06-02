package oncog.cogroom.global.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiErrorCode {

    // 공통
    INVALID_USER_ROLE("403", HttpStatus.FORBIDDEN, "유효하지 않은 사용자 권한입니다."),
    USER_NOT_FOUND("404", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_LOGOUT_USER("404", HttpStatus.NOT_FOUND, "이미 로그아웃된 사용자입니다."),

    // 회원가입, 정보 수정
    NEED_VERIFICATION("401", HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다."),
    DUPLICATE_USER_EMAIL("409", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_USER_NICKNAME("409", HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    // 카카오 로그인
    KAKAO_INVALID_KEY_TYPE("400", HttpStatus.BAD_REQUEST, "일치하는 공개키 타입이 없습니다."),
    KAKAO_PUBLIC_KEY_NOT_FOUND("400", HttpStatus.BAD_REQUEST, "해당 KEY에 대한 공개키를 찾을 수 없습니다."),
    KAKAO_UNSUPPORTED_KEY_TYPE("400", HttpStatus.BAD_REQUEST, "지원하지 않는 KEY 형식입니다."),
    KAKAO_INVALID_AUTHORIZATION_CODE("400", HttpStatus.BAD_REQUEST, "유효하지 않은 Authorization Code입니다."),
    KAKAO_INVALID_CLIENT_SECRET("401", HttpStatus.UNAUTHORIZED, "클라이언트 시크릿 키가 올바르지 않습니다."),
    KAKAO_AUTH_FAILED("401", HttpStatus.UNAUTHORIZED, "카카오 인증 요청이 실패했습니다."),

    // JWT 토큰
    INVALID_TOKEN("401", HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    ALREADY_BLACK_LIST("401", HttpStatus.UNAUTHORIZED, "이미 블랙리스트에 포함된 토큰입니다."),
    EXPIRED_TOKEN("401", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 시스템
    INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
