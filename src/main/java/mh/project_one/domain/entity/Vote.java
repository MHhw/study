package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote",
        uniqueConstraints = { // 한 사용자는 하나의 대상에 대해 한 번만 투표 가능
                @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 투표한 사용자

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // "QUESTION" 또는 "ANSWER"

    @Column(name = "target_id", nullable = false)
    private Long targetId; // Question ID 또는 Answer ID

    @Column(name = "vote_type", nullable = false)
    private short voteType; // 1: 추천, -1: 비추천, (0: 중립 - 선택적)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 다형적 연관관계를 위한 필드 (선택적, mappedBy에서 사용하기 위함)
    // Comment 엔티티와 유사하게, Question/Answer 엔티티에서 mappedBy를 위해 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "question_id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Question questionTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "answer_id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Answer answerTarget;

    @Builder
    public Vote(User user, String targetType, Long targetId, short voteType, Question questionTarget, Answer answerTarget) {
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
        this.voteType = voteType;
        // targetType에 따라 questionTarget 또는 answerTarget 설정
        if ("QUESTION".equals(targetType) && questionTarget != null && questionTarget.getQuestionId().equals(targetId)) {
            this.questionTarget = questionTarget;
        } else if ("ANSWER".equals(targetType) && answerTarget != null && answerTarget.getAnswerId().equals(targetId)) {
            this.answerTarget = answerTarget;
        }
    }

    // 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }

    // voteType 변경 메서드 (예: 추천했다가 비추천으로 변경)
    public void changeVoteType(short newVoteType) {
        this.voteType = newVoteType;
        // this.updatedAt은 @UpdateTimestamp에 의해 자동으로 갱신됨
    }

    // Question, Answer 엔티티에서 mappedBy를 사용하기 위한 setter (실제 사용 시 주의)
    public void setQuestionTarget(Question questionTarget) {
        if (questionTarget != null && "QUESTION".equals(this.targetType) && questionTarget.getQuestionId().equals(this.targetId)) {
            this.questionTarget = questionTarget;
        }
    }

    public void setAnswerTarget(Answer answerTarget) {
        if (answerTarget != null && "ANSWER".equals(this.targetType) && answerTarget.getAnswerId().equals(this.targetId)) {
            this.answerTarget = answerTarget;
        }
    }
}