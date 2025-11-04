package mh.project_one.domain.service.user;

import lombok.RequiredArgsConstructor;
import mh.project_one.domain.entity.user.User;
import mh.project_one.domain.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public User registerSocialUser(String provider, String providerId, String email, String nickname, String imageUrl) {
        String username = StringUtils.hasText(email)
                ? email
                : provider.toLowerCase(Locale.ROOT) + "_" + providerId; // 공급자+아이디로 기본 계정 생성

        String displayName = StringUtils.hasText(nickname) ? nickname : username;

        User user = User.builder()
                .username(username)
                .email(StringUtils.hasText(email) ? email : username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 소셜 계정은 난수 비밀번호 발급
                .nickname(displayName)
                .profileImageUrl(imageUrl)
                .role("ROLE_USER")
                .provider(provider)
                .providerId(providerId)
                .isActive(true)
                .isLocked(false)
                .lastLoginAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User synchronizeSocialUser(User user, String provider, String providerId, String email, String nickname, String imageUrl) {
        user.updateSocialInfo(provider, providerId, email, nickname, imageUrl);
        user.updateLastLogin(LocalDateTime.now());
        return user;
    }

    @Transactional
    public void updateLastLogin(User user) {
        user.updateLastLogin(LocalDateTime.now());
    }

    @Transactional
    public User registerLocalUser(User user, String rawPassword) {
        user.updatePassword(passwordEncoder.encode(rawPassword));
        user.updateLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
}
