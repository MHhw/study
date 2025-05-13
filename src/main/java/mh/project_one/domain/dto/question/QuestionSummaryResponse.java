package mh.project_one.domain.dto.question;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴 사용 (선택 사항이지만, 생성 시 가독성을 높여줍니다)
public class QuestionSummaryResponse {

    private Long id; // 게시글 ID
    private String title; // 제목
    private String authorNickname; // 작성자 닉네임
    private int answerCount; // 답변 수
    private int viewCount; // 조회수
    private int voteCount; // 추천 수
    private List<String> tags; // 태그 목록 (간단히 문자열 리스트로)
    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 최종 수정일 (목록에는 보통 작성일만 표시하나, 필요에 따라 포함)

    // 만약 서비스 레이어에서 Entity를 DTO로 변환하는 로직을 여기에 두고 싶다면,
    // 정적 팩토리 메서드나 생성자를 추가할 수 있습니다.
    // 예: public static QuestionSummaryResponse from(Question question, List<String> tags) { ... }
}