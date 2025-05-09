package mh.project_one.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 핵심 설정 클래스
 * 웹 보안 설정 활성화
 * HTTP 요청에 대한 보안 규칙, 폼 로그인, 로그아웃 등 구성
 */
@Configuration
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {

    /**
     * 사용자 정보를 로드하는 서비스
     * 사용자 인증 시 이 서비스를 통해 사용자 정보 조회
     */
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler){
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    /**
     * 비밀번호 암호화에 사용될 PasswordEncoder 빈 등록
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 요청에 대한 보안 필터 체인 설정
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 인스턴스
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // .requestMatchers("/login", "/perform_login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        //.successHandler(customAuthenticationSuccessHandler())
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .build();
    }

    /**
     * 사용자 로그인 시 loginProcessingUrl가 작동하며 실제 인증은 등록된 AuthenticationProvider를 통해 수행됨
     * @param http HttpSecurity 객체
     * @return AuthenticationManager 인스턴스
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     * 커스텀 AuthenticationProvider를 빈으로 등록하여 사용자 인증 처리
     * @return CustomAuthenticationProvider 인스턴스
     */
    @Bean
    AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        return new CustomAuthenticationProvider(customUserDetailsService, passwordEncoder);
    }

}
