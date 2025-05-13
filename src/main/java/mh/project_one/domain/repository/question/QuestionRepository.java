package mh.project_one.domain.repository.question;

import mh.project_one.domain.entity.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // (선택 사항이지만 명시적으로 추가 가능)

import java.util.List;
import java.util.Optional;

@Repository // 이 인터페이스가 Spring Data JPA 리포지토리임을 명시 (Spring Boot에서는 생략 가능하기도 함)
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // JpaRepository를 상속받는 것만으로 기본적인 CRUD 메소드 사용 가능
    // 여기에 추가적인 쿼리 메소드를 직접 정의할 수도 있습니다.
    // 예: List<Question> findByTitle(String title);
}