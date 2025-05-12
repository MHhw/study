package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Answer;
import mh.project_one.domain.entity.Question;
import mh.project_one.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 질문에 달린 답변 목록 조회 (페이징 없이)
    List<Answer> findByQuestionOrderByCreatedAtAsc(Question question); // 작성일 오름차순 정렬

    // 특정 질문에 달린 답변 목록 페이징 조회
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    // 특정 사용자가 작성한 답변 목록 페이징 조회
    Page<Answer> findByUser(User user, Pageable pageable);
}