package mh.project_one.domain.entity.question;

import jakarta.persistence.*; // JPA 어노테이션을 위해 추가
import lombok.*; // 다른 Lombok 어노테이션 추가
import mh.project_one.domain.entity.common.BaseTimeEntity; // BaseTimeEntity 상속을 위해 추가
import mh.project_one.domain.entity.user.User; // User와의 연관관계를 위해 추가

@Getter
@Setter // 엔티티의 필드 변경이 필요할 수 있으므로 Setter 추가 (필요에 따라 선택적 사용 또는 특정 필드만 열어두는 방식 고려)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "question") // 데이터베이스의 "question" 테이블과 매핑
public class Question extends BaseTimeEntity { // createdAt, updatedAt 필드를 상속받음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    // 다대일(N:1) 관계: 여러 Question이 하나의 User에 매핑될 수 있음
    // fetch = FetchType.LAZY: Question 조회 시 User 정보는 실제로 접근할 때 로드 (성능 최적화)
    // JoinColumn: 외래 키(FK)를 매핑할 때 사용
    // name = "user_id": Question 테이블의 FK 컬럼명
    // nullable = false: User는 필수 정보이므로 NOT NULL 제약조건
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT 타입 명시
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0; // 필드 선언 시 기본값 할당

    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0; // 필드 선언 시 기본값 할당

    // User 엔티티의 addQuestion 메소드에서 question.setUser(this)를 호출하므로
    // 여기서는 별도의 연관관계 편의 메소드가 필수는 아님.
    // 다만, Question 객체 생성 후 User를 설정해야 할 경우를 위해 Setter(Lombok @Setter)를 열어두거나
    // 빌더를 통해 user 필드를 설정할 수 있도록 함.
}