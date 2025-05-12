package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Question;
import mh.project_one.domain.entity.QuestionTag;
import mh.project_one.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> { // QuestionTag의 ID 타입이 Long이라고 가정

    // 특정 질문에 연결된 모든 QuestionTag 조회
    List<QuestionTag> findByQuestion(Question question);

    // 특정 태그에 연결된 모든 QuestionTag 조회
    List<QuestionTag> findByTag(Tag tag);

    // 특정 질문과 특정 태그로 QuestionTag 조회 (매핑 존재 여부 확인용)
    Optional<QuestionTag> findByQuestionAndTag(Question question, Tag tag);

    // 특정 질문 ID로 연결된 모든 QuestionTag 삭제 (질문 수정 시 태그 변경에 사용 가능)
    void deleteByQuestion(Question question);
}