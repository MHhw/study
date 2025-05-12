package mh.project_one.domain.dto.response.question;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import mh.project_one.domain.dto.response.answer.AnswerResponse; // Answer DTO 경로에 맞게 수정
import mh.project_one.domain.dto.response.comment.CommentResponse; // Comment DTO 경로에 맞게 수정
import mh.project_one.domain.dto.response.tag.TagResponse; // Tag DTO 경로에 맞게 수정

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDetailResponse {

    private Long id; // 게시글 ID
    private String title; // 제목
    private String content; // 내용 (HTML 또는 Markdown)
    private Long authorId; // 작성자 내부 ID (수정/삭제 권한 확인 등 내부 로직용)
    private String authorNickname; // 작성자 닉네임
    // private String authorProfileImageUrl; // 작성자 프로필 이미지 URL (선택 사항)
    private int viewCount; // 조회수
    private int voteCount; // 추천 수
    private List<TagResponse> tags; // 태그 상세 정보 목록
    private List<AnswerResponse> answers; // 답변 목록
    private List<CommentResponse> commentsOnQuestion; // 게시글 자체에 대한 댓글 목록 (선택 사항)
    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 최종 수정일

    // 첨부파일 정보 등도 필요에 따라 추가
    // private List<AttachmentResponse> attachments;

    // 예: public static QuestionDetailResponse from(Question question, List<TagResponse> tags, List<AnswerResponse> answers, ...) { ... }
}