package oncog.cogroom.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        BaseErrorCode errorCode = (BaseErrorCode) (request.getAttribute("errorCode"));

        if (errorCode == null) {
            errorCode = AuthErrorCode.INVALID_TOKEN;
        }

        ApiErrorResponse errorResponse = ApiErrorResponse.of(errorCode);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}