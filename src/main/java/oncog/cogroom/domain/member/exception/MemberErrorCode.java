package oncog.cogroom.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode implements BaseErrorCode {
    NICKNAME_DUPLICATE_ERROR("NICKNAME_DUPLICATE_ERROR", HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    NICKNAME_INVALID_PATTERN("NICKNAME_INVALID_PATTERN", HttpStatus.BAD_REQUEST,"닉네임은 형식이 잘못되었습니다."),
    MEMBER_NOT_FOUND_ERROR("MEMBER_NOT_FOUND_ERROR", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PASSWORD_PATTERN_ERROR("PASSWORD_PATTERN_ERROR", HttpStatus.BAD_REQUEST, "비밀번호는 영문 소문자, 대문자, 특수 문자로 구성되어야 합니다."),
    PHONENUMBER_PATTERN_ERROR("PHONENUMBER_PATTERN_ERROR", HttpStatus.BAD_REQUEST, "닉네임은 한글,영문,숫자만 사용할 수 있습니다."),
    SIZE_ERROR("SIZE_ERROR", HttpStatus.BAD_REQUEST, "길이가 초과되었습니다.");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
