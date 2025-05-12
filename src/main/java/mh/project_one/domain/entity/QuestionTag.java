package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_tag",
        uniqueConstraints = { // 복합 유니크 제약조건 (DB 스키마의 PK와 유사한 역할)
                @UniqueConstraint(columnNames = {"question_id", "tag_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_tag_id") // 별도의 PK를 가질 수도 있고, 복합키를 사용할 수도 있음. 여기서는 별도 PK 사용.
    private Long id; // DB 스키마에서는 (question_id, tag_id)를 PK로 했지만, JPA에서는 단일 ID를 권장.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    // 생성자
    public QuestionTag(Question question, Tag tag) {
        this.question = question;
        this.tag = tag;
    }

    // 연관관계 편의 메서드 (Setter 역할)
    public void setQuestion(Question question) {
        this.question = question;
        // 필요시 question.getQuestionTags().add(this) 와 같은 로직 추가 (무한루프 주의)
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        // 필요시 tag.getQuestionTags().add(this) 와 같은 로직 추가 (무한루프 주의)
    }

    // Builder 패턴을 사용하려면 @Builder 어노테이션과 해당 생성자 추가
    // 예:
    // @Builder
    // public QuestionTag(Question question, Tag tag) {
    //     this.question = question;
    //     this.tag = tag;
    // }
}