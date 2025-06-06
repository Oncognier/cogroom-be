package oncog.cogroom.global.common.service;

import oncog.cogroom.domain.auth.exception.AuthException;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected JwtProvider jwtProvider;

    protected Long getMemberId() {
        return jwtProvider.extractMemberId()
                .orElseThrow(() -> new AuthException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
