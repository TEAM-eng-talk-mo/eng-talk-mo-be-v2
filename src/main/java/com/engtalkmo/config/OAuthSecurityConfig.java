package com.engtalkmo.config;

import com.engtalkmo.config.jwt.TokenAuthenticationFilter;
import com.engtalkmo.config.jwt.TokenProvider;
import com.engtalkmo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.engtalkmo.config.oauth.OAuth2SuccessHandler;
import com.engtalkmo.config.oauth.OAuth2UserCustomService;
import com.engtalkmo.domain.refreshtoken.RefreshTokenRepository;
import com.engtalkmo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class OAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증 비활성화 (Session 을 사용하지 않고, Rest API 방식을 사용)
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 비활성화
                .logout(AbstractHttpConfigurer::disable)

                .cors(configurer -> configurer.configure(http)) // CORS 활성화
                .sessionManagement(configurer -> configurer // 세션관리 정책 STATELESS (세션이 있으면 쓰지도 않고, 없으면 만들지도 않는다.)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // header 를 확인 할 custom filter 추가

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/sign-up", "/user").permitAll()
                        .requestMatchers("/api/token").permitAll()
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(configurer -> configurer
                        .authorizationEndpoint(config -> config // Authorization 요청과 관련된 상태 저장
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행 할 핸들러
                        .userInfoEndpoint(config -> config
                                .userService(oAuth2UserCustomService)))

                .logout(configurer -> configurer
                        .logoutSuccessUrl("/hello"))

                .exceptionHandling(configurer -> configurer // "/api/**"로 요청되는 경우 401 상태코드를 반환하도록 예외 처리
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**")))
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userRepository);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
