package mh.project_one.domain.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mh.project_one.domain.entity.question.Question;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummaryResponse {

    private Long questionId;        // 질문 ID (상세보기 링크 등에 사용)
    private String title;           // 질문 제목
    private String authorNickname;  // 작성자 닉네임 (User 엔티티에서 가져옴)
    private LocalDateTime createdAt;  // 작성일시 (BaseTimeEntity에서 상속받음)
    private int viewCount;          // 조회수
    private int voteCount;          // 추천수

    /**
     * Question 엔티티 객체를 QuestionSummaryResponse DTO 객체로 변환하는 정적 팩토리 메서드입니다.
     * 서비스 계층에서 이 메서드를 사용하여 엔티티를 DTO로 변환할 수 있습니다.
     *
     * @param question 변환할 Question 엔티티 객체
     * @return 변환된 QuestionSummaryResponse DTO 객체
     */
    public static QuestionSummaryResponse fromEntity(Question question) {
        // Question 엔티티와 연관된 User 엔티티가 null인 경우를 대비하여 null 체크를 하는 것이 안전합니다.
        // (Question 엔티티 정의에서 user 필드는 nullable = false 이므로 이론상 null이 될 수 없지만, 방어적으로 코딩)
        String nickname = (question.getUser() != null) ? question.getUser().getNickname() : "알 수 없음";

        return new QuestionSummaryResponse(
                question.getQuestionId(),
                question.getTitle(),
                nickname,
                question.getCreatedAt(), // BaseTimeEntity 로부터 상속받은 필드
                question.getViewCount(),
                question.getVoteCount()
        );
    }
}