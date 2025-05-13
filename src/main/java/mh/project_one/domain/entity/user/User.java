package mh.project_one.domain.entity.user;

import jakarta.persistence.*; // JPA 어노테이션을 위해 추가
import lombok.*; // 다른 Lombok 어노테이션 추가
import mh.project_one.domain.entity.common.BaseTimeEntity; // BaseTimeEntity 상속을 위해 추가
import mh.project_one.domain.entity.question.Question; // Question과의 연관관계를 위해 추가

import java.time.LocalDateTime; // lastLoginAt 필드를 위해 추가
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 PROTECTED 접근 수준의 기본 생성자 권장
@AllArgsConstructor // 모든 필드를 사용하는 생성자 (빌더 패턴 사용 시 유용)
@Builder // 빌더 패턴을 사용하기 위해 추가
@Entity // 이 클래스가 JPA 엔티티임을 선언
@Table(name = "users") // 데이터베이스의 "users" 테이블과 매핑
public class User extends BaseTimeEntity { // createdAt, updatedAt 필드를 상속받음

    @Id // 기본 키(PK) 필드임을 선언
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스의 IDENTITY 전략 사용 (예: PostgreSQL의 SERIAL, MySQL의 AUTO_INCREMENT)
    @Column(name = "user_id") // 실제 DB 컬럼명과 매핑 (선택적, 필드명과 동일하면 생략 가능)
    private Long userId;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default // 빌더 사용 시 이 초기값이 기본으로 적용됩니다.
    private String role = "ROLE_USER"; // 필드 선언 시 기본값 할당 (객체 생성 시 적용)
    // Enum 타입으로 관리하는 것이 더 좋습니다.

    @Column(length = 50)
    @Builder.Default // 빌더 사용 시 이 초기값이 기본으로 적용됩니다.
    private String provider = "LOCAL"; // 필드 선언 시 기본값 할당

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // TIMESTAMP WITH TIME ZONE

    @Column(name = "is_active", nullable = false)
    @Builder.Default // 빌더 사용 시 이 초기값이 기본으로 적용됩니다.
    private boolean isActive = true; // 필드 선언 시 기본값 할당

    @Column(name = "is_locked", nullable = false)
    @Builder.Default // 빌더 사용 시 이 초기값이 기본으로 적용됩니다.
    private boolean isLocked = false; // 필드 선언 시 기본값 할당

    // User와 Question 간의 양방향 관계 설정 (User가 여러 Question을 가질 수 있음)
    // mappedBy: 연관관계의 주인이 Question 엔티티의 'user' 필드임을 명시
    // cascade: User 엔티티의 생명주기 변경(저장, 삭제 등)이 Question에도 전파되도록 설정 (ALL은 모든 작업 전파)
    // orphanRemoval: 부모(User)와의 관계가 끊어진 자식(Question) 엔티티를 DB에서 자동 삭제
    // fetch: LAZY 로딩을 통해 User 조회 시 Question 목록은 실제로 접근할 때 로드 (성능 최적화)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // 빌더 사용 시 questions 필드를 초기화
    private List<Question> questions = new ArrayList<>();

    // 연관관계 편의 메소드 (선택 사항이지만, 양방향 관계 설정 시 유용)
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setUser(this); // Question 엔티티에도 User를 설정 (연관관계의 주인 쪽)
    }
}