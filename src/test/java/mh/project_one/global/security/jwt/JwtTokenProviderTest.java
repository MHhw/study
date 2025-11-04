package mh.project_one.global.security.jwt;

import mh.project_one.global.security.SecurityUser;
import mh.project_one.global.security.SecurityUserRepository;
import mh.project_one.global.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SecurityUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private SecurityUser savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        SecurityUser user = SecurityUser.builder()
                .username("jwt-user")
                .password(passwordEncoder.encode("password"))
                .email("jwt-user@example.com")
                .nickname("jwt-user")
                .role("ROLE_USER")
                .provider("LOCAL")
                .providerId("jwt-user")
                .isActive(true)
                .isLocked(false)
                .build();
        user.updateLastLogin(LocalDateTime.now());
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("JWT 토큰을 생성하고 인증 객체를 복원한다")
    void createAndValidateToken() {
        UserPrincipal principal = UserPrincipal.create(savedUser);

        String accessToken = jwtTokenProvider.createAccessToken(principal);

        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isInstanceOf(UserPrincipal.class);

        UserPrincipal authenticatedPrincipal = (UserPrincipal) authentication.getPrincipal();
        assertThat(authenticatedPrincipal.getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(authenticatedPrincipal.getId()).isEqualTo(savedUser.getUserId());
    }
}
