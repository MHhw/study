package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // 테이블명 명시 (클래스명과 다를 경우)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 함 (protected 접근 수준 권장)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL의 BIGSERIAL과 매핑 (IDENTITY 전략 사용)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(nullable = false, length = 20)
    private String role; // 예: "ROLE_USER", "ROLE_ADMIN" (enum으로 관리하는 것도 좋음)

    @Column(length = 50)
    private String provider; // 예: "LOCAL", "GOOGLE"

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // 엔티티 수정 시 자동으로 현재 시간 저장
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // 기본값 설정

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false; // 기본값 설정

    // 양방향 연관관계 설정 (User가 작성한 Question 목록)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 작성한 Answer 목록)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 작성한 Comment 목록)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 한 Vote 목록)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 받은 Notification 목록)
    @OneToMany(mappedBy = "receiverUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> receivedNotifications = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 발생시킨 Notification 목록 - sender)
    @OneToMany(mappedBy = "senderUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sentNotifications = new ArrayList<>();

    // 양방향 연관관계 설정 (User가 업로드한 Attachment 목록)
    @OneToMany(mappedBy = "uploaderUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();


    @Builder // 빌더 패턴 사용
    public User(String username, String password, String email, String nickname, String role, String provider, String providerId, boolean isActive, boolean isLocked) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.isActive = isActive;
        this.isLocked = isLocked;
    }

    // 연관관계 편의 메서드 (양방향 관계 설정 시 필요할 수 있음)
    public void addQuestion(Question question) {
        this.questions.add(question);
        if (question.getUser() != this) { // 무한 루프 방지
            question.setUser(this);
        }
    }
    // ... 다른 연관관계 편의 메서드들도 유사하게 추가 ...
}