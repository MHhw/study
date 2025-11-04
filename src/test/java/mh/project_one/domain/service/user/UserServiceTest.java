package mh.project_one.domain.service.user;

import mh.project_one.domain.entity.user.User;
import mh.project_one.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("소셜 로그인 신규 사용자를 생성하고 암호화된 비밀번호를 저장한다")
    void registerSocialUser() {
        User saved = userService.registerSocialUser("GOOGLE", "12345", "test@example.com", "tester", "image-url");

        Optional<User> optionalUser = userRepository.findById(saved.getUserId());
        assertThat(optionalUser).isPresent();
        User user = optionalUser.get();

        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertThat(user.getProvider()).isEqualTo("GOOGLE");
        assertThat(user.getProviderId()).isEqualTo("12345");
        assertThat(user.getPassword()).isNotBlank(); // 암호화된 문자열이 저장됨을 확인
    }

    @Test
    @DisplayName("기존 소셜 사용자 정보를 최신 데이터로 동기화한다")
    void synchronizeSocialUser() {
        User saved = userService.registerSocialUser("GOOGLE", "12345", "test@example.com", "tester", "image-url");

        User updated = userService.synchronizeSocialUser(saved, "GOOGLE", "12345", "new@example.com", "new-name", "new-image");

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getNickname()).isEqualTo("new-name");
        assertThat(updated.getProfileImageUrl()).isEqualTo("new-image");
        assertThat(updated.isActive()).isTrue();
        assertThat(updated.isLocked()).isFalse();
    }
}
