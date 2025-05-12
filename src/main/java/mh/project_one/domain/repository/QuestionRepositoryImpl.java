package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Question;
import mh.project_one.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> { // , QuestionRepositoryCustom Querydsl 사용 시 추가

    // 특정 사용자가 작성한 질문 목록 페이징 조회
    Page<Question> findByUser(User user, Pageable pageable);

    // 제목에 특정 키워드가 포함된 질문 목록 페이징 조회
    Page<Question> findByTitleContaining(String titleKeyword, Pageable pageable);

    // 내용에 특정 키워드가 포함된 질문 목록 페이징 조회
    Page<Question> findByContentContaining(String contentKeyword, Pageable pageable);

    // 제목 또는 내용에 특정 키워드가 포함된 질문 목록 페이징 조회
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%")
    Page<Question> findByTitleContainingOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // (참고) 만약 QuestionRepositoryCustom과 함께 사용한다면,
    // JpaRepository<Question, Long>, QuestionRepositoryCustom 와 같이 다중 상속합니다.
}