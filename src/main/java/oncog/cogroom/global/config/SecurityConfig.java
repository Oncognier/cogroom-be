package oncog.cogroom.global.config;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.security.jwt.JwtAuthenticationFilter;
import oncog.cogroom.global.security.jwt.JwtProvider;
import oncog.cogroom.global.security.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);

        return http
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userDetailService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Security 기본 셋팅
    private void configureCommonSecuritySettings(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // csrf disable
                .anonymous(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable) // form login disable
                .httpBasic(AbstractHttpConfigurer::disable)  // http basic 인증 방식 disable
                .rememberMe(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}
