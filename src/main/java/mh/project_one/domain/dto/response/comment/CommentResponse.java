package mh.project_one.domain.dto.response.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id; // 댓글 ID
    private String content; // 댓글 내용
    private Long authorId; // 댓글 작성자 ID
    private String authorNickname; // 댓글 작성자 닉네임
    // private Long parentCommentId; // 부모 댓글 ID (대댓글 기능 시)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}