package mh.project_one.security;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users") // 데이터베이스 테이블명
@Getter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100, unique = true, nullable = false)
    private String username; // 로그인 아이디

    @Column(name = "password", length = 255, nullable = false)
    private String password; // 해시 암호화하여 저장, 필수

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", length = 50, unique = true, nullable = false)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "role", length = 20, nullable = false)
    private String role; // 예: "ROLE_USER", "ROLE_ADMIN"

    @Column(name = "provider", length = 50)
    private String provider; // 예: "LOCAL", "GOOGLE", "GITHUB"

    @Column(name = "provider_id", length = 255)
    private String providerId; // 소셜 로그인 제공자의 사용자 식별 ID

    @CreationTimestamp // JPA 엔티티가 생성될 때 자동으로 현재 시간으로 설정
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // JPA 엔티티가 업데이트될 때 자동으로 현재 시간으로 설정
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // 계정 활성 상태

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false; // 계정 잠금 상태

    @Builder
    public User(Long userId, String username, String password, String email, String nickname,
                String profileImageUrl, String role, String provider, String providerId,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt,
                boolean isActive, boolean isLocked) {
        this.userId = userId;
        this.username = username;
        this.password = password; // 비밀번호는 서비스단에서 암호화해서 저장해야 합니다.
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = (role == null || role.isEmpty()) ? "ROLE_USER" : role; // 기본값 설정
        this.provider = (provider == null || provider.isEmpty()) ? "LOCAL" : provider; // 기본값 설정
        this.providerId = providerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.isActive = isActive;
        this.isLocked = isLocked;
    }

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 단일 역할을 GrantedAuthority 컬렉션으로 변환
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        // UserDetails 인터페이스의 getUsername()은 일반적으로 로그인 ID를 반환합니다.
        // 여기서는 username 필드를 사용합니다.
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 테이블 스키마에 계정 만료에 대한 명시적 컬럼이 없으므로, 항상 true를 반환하거나
        // 비즈니스 로직에 따라 is_active와 연동할 수 있습니다.
        // 여기서는 단순하게 항상 true로 설정합니다. 필요시 수정하세요.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked; // is_locked가 false여야 계정이 잠기지 않은 상태
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 테이블 스키마에 자격 증명 만료에 대한 명시적 컬럼이 없으므로, 항상 true를 반환합니다.
        // 비밀번호 만료 정책이 있다면 관련 로직을 추가해야 합니다.
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive; // is_active가 true여야 계정이 활성화된 상태
    }
}
