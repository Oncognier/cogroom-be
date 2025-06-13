package oncog.cogroom.global.security.jwt;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.exception.AuthException;
import oncog.cogroom.domain.member.enums.Provider;
import oncog.cogroom.global.security.service.CustomUserDetailService;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.resolveToken(request);

            if (token != null && jwtProvider.isValid(token)) {

                Claims claims = jwtProvider.getClaims(token);
                String memberId = claims.getSubject();

                // 서비스 UUID로 사용자 조회
                UserDetails userDetails = userDetailService.loadUserByUsername(memberId);

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            }

        } catch (AuthException e) {
            request.setAttribute("errorCode", e.getErrorCode());
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request,response,null);
        }

    }


}