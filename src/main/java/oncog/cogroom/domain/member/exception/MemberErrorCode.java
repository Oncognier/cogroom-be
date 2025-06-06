package oncog.cogroom.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode implements BaseErrorCode {
    DUPLICATE_USER_NICKNAME("DUPLICATE_USER_NICKNAME", HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    INVALID_PHONE_NUMBER("INVALID_PHONE_NUMBER", HttpStatus.BAD_REQUEST,"전화번호 형식이 올바르지 않습니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
