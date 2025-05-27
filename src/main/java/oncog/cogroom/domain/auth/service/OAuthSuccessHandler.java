package oncog.cogroom.domain.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.service.MemberService;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        boolean exists = memberService.isExist(user.getAttribute("providerId"));
        Member member = memberService.find(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<Object> nickname = Optional.ofNullable(exists ? null : user.getAttribute("nickname"));
        Optional<Object> email = Optional.ofNullable(exists ? null : user.getAttribute("email"));
        Optional<SocialResponseDTO.ServiceTokenDTO> tokens = Optional.ofNullable(exists ? createTokens(member) : null);

        String redirectUri = UriComponentsBuilder
                .fromUriString("/oauth/callback")
                .queryParam("needSignup", !exists)
                .queryParam("nickname", nickname.map(nick -> URLEncoder.encode((String) nick, StandardCharsets.UTF_8)).orElse(null))
                .queryParam("email", email)
                .build()
                .toString();

        Cookie accessTokenCookie = tokens.map(serviceTokenDTO -> new Cookie("accessToken", serviceTokenDTO.getAccessToken())).orElse(null);
        Cookie refreshTokenCookie = tokens.map(serviceTokenDTO -> new Cookie("refreshToken", serviceTokenDTO.getRefreshToken())).orElse(null);

        // 쿠키 설정
        cookieSetting(accessTokenCookie);
        cookieSetting(refreshTokenCookie);

        if (accessTokenCookie != null) {
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }

        response.sendRedirect(redirectUri);
    }

    private SocialResponseDTO.ServiceTokenDTO createTokens(Member member) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .provider(member.getProvider())
                .role(MemberRole.USER)
                .memberId(member.getId())
                .memberEmail(member.getEmail())
                .build();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getMemberEmail());

        return SocialResponseDTO.ServiceTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    private void cookieSetting(Cookie cookie) {
        if (cookie != null) {
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(30 * 30);
        }
    }
}
