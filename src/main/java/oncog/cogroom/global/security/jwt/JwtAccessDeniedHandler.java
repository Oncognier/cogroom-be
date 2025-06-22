package oncog.cogroom.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ApiErrorCode forbiddenError = ApiErrorCode.FORBIDDEN_ERROR;

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(forbiddenError.getStatus().value());

        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                forbiddenError.getCode()
                , forbiddenError.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
