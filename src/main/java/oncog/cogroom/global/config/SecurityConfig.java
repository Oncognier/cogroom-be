package oncog.cogroom.global.config;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.global.security.jwt.JwtAuthenticationFilter;
import oncog.cogroom.global.security.jwt.JwtProvider;
import oncog.cogroom.global.security.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailService;

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainPermitAll(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);

        http.securityMatchers(matchers -> matchers.requestMatchers(requestPermitAll()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .permitAll());

        return http.build();
    }

    // 추후 주석 해제 예정
//    @Bean
//    @Order(2)
//    public SecurityFilterChain filterChainAuthorized(HttpSecurity http) throws Exception {
//        configureCommonSecuritySettings(http);
//
//        http.securityMatchers(matchers -> matchers.requestMatchers(requestHasRoleUser()))
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest()
//                        .hasAuthority(MemberRole.USER.name()));
//
//        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider,userDetailService),
//                UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
//    // 인증 및 인가가 필요한 엔드포인트에 적용되는 RequestMatcher
//    private RequestMatcher[] requestHasRoleUser() {
//        List<RequestMatcher> requestMatchers = List.of(
//                antMatcher("/api/v1/daily/**")
//        );
//
//        return requestMatchers.toArray(RequestMatcher[]::new);
//    }

    // permitAll 권한을 가진 엔드포인트에 적용되는 RequestMatcher
    private RequestMatcher[] requestPermitAll() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/**")
//                antMatcher("/api/v1/auth/social-login/**")
        );

        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    // Security 기본 셋팅
    private void configureCommonSecuritySettings(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // csrf disable
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // form login disable
                .httpBasic(AbstractHttpConfigurer::disable)  // http basic 인증 방식 disable
                .rememberMe(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}
