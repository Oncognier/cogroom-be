package oncog.cogroom.global.common.service;

import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.domain.AuthException;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected JwtProvider jwtProvider;

    protected Long getMemberId() {
        return jwtProvider.extractMemberId()
                .orElseThrow(() -> new AuthException(ApiErrorCode.USER_NOT_FOUND));
    }
}
