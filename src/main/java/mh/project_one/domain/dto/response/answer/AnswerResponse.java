package mh.project_one.domain.dto.response.answer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// import mh.project_one.domain.dto.response.comment.CommentResponse; // 답변에 달린 댓글 DTO

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long id; // 답변 ID
    private String content; // 답변 내용
    private Long authorId; // 답변 작성자 ID
    private String authorNickname; // 답변 작성자 닉네임
    private int voteCount; // 답변 추천 수
    // private List<CommentResponse> commentsOnAnswer; // 답변에 대한 댓글 목록 (선택 사항)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean chosen; // 채택 여부 (선택 사항)
}