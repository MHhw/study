package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Comment;
import mh.project_one.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 대상(질문 또는 답변)에 달린 최상위 댓글 목록 조회 (작성일 오름차순)
    List<Comment> findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAsc(String targetType, Long targetId);

    // 특정 대상에 달린 모든 댓글 페이징 조회 (대댓글 포함, 정렬 필요시 Pageable에 명시)
    Page<Comment> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);

    // 특정 부모 댓글에 달린 대댓글 목록 조회 (작성일 오름차순)
    List<Comment> findByParentOrderByCreatedAtAsc(Comment parent);

    // 특정 사용자가 작성한 댓글 목록 페이징 조회
    Page<Comment> findByUser(User user, Pageable pageable);
}