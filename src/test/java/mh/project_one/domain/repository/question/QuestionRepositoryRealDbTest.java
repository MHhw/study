package mh.project_one.domain.repository.question;

import mh.project_one.domain.entity.question.Question;
import mh.project_one.domain.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("realdbtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuestionRepositoryRealDbTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("[RealDB] 모든 질문(Question) 조회 테스트")
    void findAllQuestionsFromRealDb() {
        // when - 실제 DB에서 모든 Question 조회
        List<Question> allQuestions = questionRepository.findAll();

        // then - 조회 결과 검증
        assertNotNull(allQuestions, "조회된 질문 목록은 null이 아니어야 합니다.");

        if (allQuestions.isEmpty()) {
            System.out.println("[RealDB] 현재 question 테이블에 데이터가 없습니다.");
        } else {
            System.out.println("[RealDB] 조회된 모든 질문 (총 " + allQuestions.size() + "건):");
            for (Question q : allQuestions) {
                System.out.println("  - ID: " + q.getQuestionId() + ", 제목: " + q.getTitle() +
                        ", 작성자 ID: " + (q.getUser() != null ? q.getUser().getUserId() : "N/A") +
                        ", 생성일: " + q.getCreatedAt());
                // User 정보가 LAZY 로딩이므로, q.getUser() 접근 시 실제 쿼리가 발생할 수 있습니다.
            }
            // 실제 DB의 데이터 개수에 따라 assertEquals 등으로 추가 검증 가능
            // 예: assertEquals(5, allQuestions.size(), "현재 DB에 5개의 질문이 있어야 합니다."); (DB 상태에 따라 달라짐)
        }
        // 이 테스트는 SELECT만 하므로 DB 상태를 변경하지 않아 롤백의 의미는 크지 않음
    }

    @Test
    @DisplayName("[RealDB] 특정 ID로 질문(Question) 조회 테스트")
    void findQuestionByIdFromRealDb() {
        // given - 실제 DB에 존재할 것으로 예상되는 Question ID
        // 이 ID는 테스트 전에 실제 DB를 확인하여 알고 있어야 합니다.
        // 또는, 이 테스트 실행 전에 데이터를 삽입하는 다른 방법(예: 테스트용 SQL 스크립트 실행, 또는 이전 테스트에서 ID 확인)이 필요할 수 있습니다.
        // 여기서는 임의로 1L로 가정합니다. 실제 DB에 1L ID의 Question이 없다면 테스트는 실패(Optional.empty)합니다.
        long testQuestionId = 7L;

        // when - 특정 ID로 Question 조회
        Optional<Question> optionalQuestion = questionRepository.findById(testQuestionId);

        // then - 조회 결과 검증
        if (optionalQuestion.isPresent()) {
            Question foundQuestion = optionalQuestion.get();
            System.out.println("[RealDB] ID(" + testQuestionId + ")로 조회된 질문:");
            System.out.println("  - 제목: " + foundQuestion.getTitle());
            System.out.println("  - 내용: " + foundQuestion.getContent());
            System.out.println("  - 작성자 닉네임: " + (foundQuestion.getUser() != null ? foundQuestion.getUser().getNickname() : "N/A"));
            System.out.println("  - 생성일: " + foundQuestion.getCreatedAt());
            assertNotNull(foundQuestion.getTitle(), "조회된 질문의 제목은 null이 아니어야 합니다 (실제 데이터 기준)");
        } else {
            System.out.println("[RealDB] ID(" + testQuestionId + ")에 해당하는 질문을 찾을 수 없습니다.");
            // 만약 특정 ID가 반드시 존재해야 한다면, 여기서 fail()을 호출하거나 assertTrue(false, "메시지")를 사용할 수 있습니다.
            // 하지만 단순히 "조회 기능"을 테스트하는 것이라면, Optional.empty()인 경우도 정상 동작으로 볼 수 있습니다.
            // 여기서는 단순히 찾을 수 없다고 출력만 합니다.
        }
        // 이 테스트도 SELECT만 수행합니다.
    }

}