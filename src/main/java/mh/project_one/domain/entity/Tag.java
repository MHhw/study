package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Tag와 Question의 관계: 다대다. QuestionTag 중간 엔티티를 통해 매핑됨.
    // Tag 엔티티에서 직접 Question 목록을 참조할 필요가 없다면 이 필드는 생략 가능.
    // 필요하다면 QuestionTag를 통해 Question 목록을 가져오는 로직은 서비스 계층에서 처리.
    // 여기서는 양방향 관계를 위해 QuestionTag 목록을 추가합니다.
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags = new HashSet<>();

    @Builder
    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 연관관계 편의 메서드
    public void addQuestionTag(QuestionTag questionTag) {
        this.questionTags.add(questionTag);
        if (questionTag.getTag() != this) {
            questionTag.setTag(this);
        }
    }
}