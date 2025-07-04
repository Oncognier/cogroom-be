package oncog.cogroom.global.config;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.global.security.jwt.JwtAccessDeniedHandler;
import oncog.cogroom.global.security.jwt.JwtAuthenticationEntryPoint;
import oncog.cogroom.global.security.jwt.JwtAuthenticationFilter;
import oncog.cogroom.global.security.jwt.JwtProvider;
import oncog.cogroom.global.security.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainAdmin(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);

        http.securityMatchers(matchers -> matchers.requestMatchers(requestHasRoleAdmin()))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAuthority(MemberRole.ADMIN.name()));

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtProvider, userDetailService, jwtAuthenticationEntryPoint),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChainAuthorized(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        // User 권한 적용
        http.securityMatchers(matchers -> matchers.requestMatchers(requestHasRoleUser()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .hasAnyAuthority(MemberRole.USER.name(), MemberRole.ADMIN.name(), MemberRole.CONTENT_PROVIDER.name()));

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider,userDetailService,jwtAuthenticationEntryPoint),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain filterChainPermitAll(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        http.securityMatchers(matchers -> matchers.requestMatchers(requestPermitAll()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .permitAll());
        return http.build();
    }

    // 인증 및 인가가 필요한 엔드포인트에 적용되는 RequestMatcher
    private RequestMatcher[] requestHasRoleUser() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/api/v1/auth/logout"),
                antMatcher("/api/v1/members/**"),
                antMatcher("/api/v1/streaks/**"),
                antMatcher("/api/v1/daily/**"),
                antMatcher("/api/v1/files/**")
        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    private RequestMatcher[] requestHasRoleAdmin() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/api/v1/admin/**")
        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }


    // permitAll 권한을 가진 엔드포인트에 적용되는 RequestMatcher
    private RequestMatcher[] requestPermitAll() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/healthcheck"),
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
                .cors(Customizer.withDefaults()) // CORS 설정 추가
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 미사용 옵션 설정
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://api.cogroom.com", "https://preview.cogroom.com", "https://cogroom-preview/**", "https://staging.cogroom.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization")); // Authorization 헤더 노출되도록 설정
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}