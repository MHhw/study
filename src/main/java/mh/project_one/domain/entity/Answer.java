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
import java.util.List;

@Entity
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 이 답변이 달린 Question 엔티티 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 답변 작성자 User 엔티티 참조

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_chosen", nullable = false)
    @ColumnDefault("false")
    private boolean isChosen = false;

    @Column(name = "vote_count", nullable = false)
    @ColumnDefault("0")
    private int voteCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Answer와 Comment의 관계: Answer 하나에 여러 Comment (일대다)
    // Comment 엔티티에서 targetType='ANSWER' 및 targetId로 구분
    @OneToMany(mappedBy = "answerTarget", cascade = CascadeType.ALL, orphanRemoval = true) // Comment 엔티티의 필드명에 따라 수정 필요
    private List<Comment> comments = new ArrayList<>();

    // Answer와 Vote의 관계: Answer 하나에 여러 Vote (일대다)
    // Vote 엔티티에서 targetType='ANSWER' 및 targetId로 구분
    @OneToMany(mappedBy = "answerTarget", cascade = CascadeType.ALL, orphanRemoval = true) // Vote 엔티티의 필드명에 따라 수정 필요
    private List<Vote> votes = new ArrayList<>();


    @Builder
    public Answer(Question question, User user, String content, boolean isChosen) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.isChosen = isChosen;
        // voteCount 등은 기본값 또는 로직에 따라 초기화
    }

    // 연관관계 편의 메서드
    public void setQuestion(Question question) {
        this.question = question;
        // 양방향 관계일 경우, question.getAnswers().add(this) 와 같은 로직도 추가 가능
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getAnswerTarget() != this) { // Comment 엔티티의 연관 필드명에 맞춰야 함
            comment.setAnswerTarget(this);
        }
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        if (vote.getAnswerTarget() != this) { // Vote 엔티티의 연관 필드명에 맞춰야 함
            vote.setAnswerTarget(this);
        }
    }

    // 채택 상태 변경 메서드
    public void markAsChosen(boolean chosen) {
        this.isChosen = chosen;
    }

    // voteCount 업데이트 메서드
    public void updateVoteCount(int newVoteCount) {
        this.voteCount = newVoteCount;
    }
}