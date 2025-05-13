package mh.project_one.global.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
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
     * 로그인 성공 시
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler();
    }

    /**
     * 로그인 실패 시
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler();
    }

    /**
     * HTTP 요청에 대한 보안 필터 체인 설정
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 인스턴스
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // 정적 리소스, 로그인 페이지 등은 인증 없이 접근 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/perform_login", "/favicon.ico").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지 경로
                        .loginProcessingUrl("/perform_login") // 로그인 처리 URL (폼의 action 속성과 일치)
                        .usernameParameter("username") // 사용자 이름 파라미터명 (폼의 input name과 일치)
                        .passwordParameter("password") // 비밀번호 파라미터명 (폼의 input name과 일치)
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 리다이렉트 될 기본 URL
                        .successHandler(authenticationSuccessHandler()) // 성공 핸들러
                        .failureHandler(authenticationFailureHandler()) // 실패 핸들러
                        .permitAll() // 로그인 페이지 접근은 모두 허용
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 처리 URL
                        .logoutSuccessUrl("/login?logout=true") // 로그아웃 성공 시 리다이렉트 될 URL
                        .invalidateHttpSession(true) // HTTP 세션 무효화
                        .deleteCookies("JSESSIONID") // 특정 쿠키 삭제
                        .permitAll()
                )
                // UserDetailsService를 사용하도록 Security에 명시적으로 알려줄 수 있지만,
                // UserDetailsService 빈이 하나만 존재하고, DaoAuthenticationProvider가 자동 구성될 때는 생략 가능합니다.
                .userDetailsService(userDetailsService);

        return http.build();
    }

    // Spring Boot 3.x부터는 AuthenticationManager를 AuthenticationConfiguration을 통해 주입받는 것이 표준입니다.
    // DaoAuthenticationProvider가 UserDetailsService와 PasswordEncoder를 사용하여 자동으로 구성됩니다.
    // 따라서 CustomAuthenticationProvider나 명시적인 AuthenticationManagerBuilder 설정이 필요 없습니다.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
