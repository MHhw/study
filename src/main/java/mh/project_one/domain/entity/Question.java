package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 컬럼명과 null 불가 설정
    private User user; // 작성자 User 엔티티 참조

    @Column(nullable = false, length = 255)
    private String title;

    @Lob // TEXT 타입 매핑 (긴 내용)
    @Column(nullable = false, columnDefinition = "TEXT") // 명시적으로 TEXT 타입 지정
    private String content;

    @Column(name = "view_count", nullable = false)
    @ColumnDefault("0") // 데이터베이스 레벨 기본값 (JPA 엔티티 레벨에서도 초기화 가능)
    private int viewCount = 0;

    @Column(name = "vote_count", nullable = false)
    @ColumnDefault("0")
    private int voteCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Question과 Answer의 관계: Question 하나에 여러 Answer (일대다)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // Question과 Tag의 관계: 다대다. 중간 테이블 QuestionTag를 엔티티로 직접 매핑하는 방식
    // 또는 @ManyToMany를 사용할 수도 있습니다. 여기서는 QuestionTag 엔티티를 별도로 만든다고 가정하고,
    // Question에서 직접 QuestionTag 목록을 가지도록 합니다.
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags = new HashSet<>();

    // Question과 Comment의 관계: Question 하나에 여러 Comment (일대다)
    // Comment 엔티티에서 targetType='QUESTION' 및 targetId로 구분
    @OneToMany(mappedBy = "questionTarget", cascade = CascadeType.ALL, orphanRemoval = true) // Comment 엔티티의 필드명에 따라 수정 필요
    private List<Comment> comments = new ArrayList<>();

    // Question과 Vote의 관계: Question 하나에 여러 Vote (일대다)
    // Vote 엔티티에서 targetType='QUESTION' 및 targetId로 구분
    @OneToMany(mappedBy = "questionTarget", cascade = CascadeType.ALL, orphanRemoval = true) // Vote 엔티티의 필드명에 따라 수정 필요
    private List<Vote> votes = new ArrayList<>();


    @Builder
    public Question(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        // viewCount, voteCount 등은 기본값 또는 로직에 따라 초기화
    }

    // 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        // 양방향 관계일 경우, user.getQuestions().add(this) 와 같은 로직도 추가 가능 (무한루프 주의)
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
        if (answer.getQuestion() != this) {
            answer.setQuestion(this);
        }
    }

    public void addQuestionTag(QuestionTag questionTag) {
        this.questionTags.add(questionTag);
        if (questionTag.getQuestion() != this) {
            questionTag.setQuestion(this);
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getQuestionTarget() != this) {
            comment.setQuestionTarget(this); // Comment 엔티티의 연관 필드명에 맞춰야 함
        }
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        if (vote.getQuestionTarget() != this) { // Vote 엔티티의 연관 필드명에 맞춰야 함
            vote.setQuestionTarget(this);
        }
    }

    // 기타 필요한 비즈니스 메서드 추가 가능 (예: 조회수 증가)
    public void incrementViewCount() {
        this.viewCount++;
    }

    // voteCount 업데이트 메서드 (Vote 서비스에서 호출될 수 있음)
    public void updateVoteCount(int newVoteCount) {
        this.voteCount = newVoteCount;
    }
}