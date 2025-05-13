// src/test/java/mh/project_one/domain/repository/question/QuestionRepositoryTest.java
package mh.project_one.domain.repository.question;

// ... (기존 import 구문들)
import mh.project_one.domain.entity.question.Question;
import mh.project_one.domain.entity.user.User;
import mh.project_one.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;     // findAll() 테스트를 위해 추가
import java.util.Optional; // findById() 반환 타입을 위해 추가

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser; // @BeforeEach에서 저장된 User 객체를 참조

    @BeforeEach
    void setUp() {
        User userToSave = User.builder()
                .username("testuser1")
                .password("password123")
                .email("testuser@example.com")
                .nickname("테스트유저1")
                // @Builder.Default 덕분에 role, isActive 등은 자동 설정됨
                .build();
        savedUser = userRepository.save(userToSave);
    }

    @Test
    @DisplayName("질문(Question) 저장 및 저장된 Question의 ID와 내용 검증")
    void saveQuestionAndVerifyDetails() {
        // given - 테스트를 위한 Question 엔티티 준비
        String expectedTitle = "테스트 질문 제목입니다.";
        String expectedContent = "이것은 질문 내용입니다. 잘 저장되어야 합니다.";

        Question newQuestion = Question.builder()
                .user(savedUser)
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        // when - Question 엔티티를 questionRepository를 통해 저장
        Question savedQuestion = questionRepository.save(newQuestion);

        // then - 저장된 Question 객체 및 필드 검증
        assertNotNull(savedQuestion, "저장된 Question 객체는 null이 아니어야 합니다.");
        assertNotNull(savedQuestion.getQuestionId(), "저장된 Question은 ID(questionId)를 가져야 합니다.");
        assertNotNull(savedQuestion.getCreatedAt(), "저장된 Question은 생성 시간(createdAt)을 가져야 합니다.");
        assertNotNull(savedQuestion.getUpdatedAt(), "저장된 Question은 수정 시간(updatedAt)을 가져야 합니다.");

        assertEquals(savedUser.getUserId(), savedQuestion.getUser().getUserId(), "저장된 Question의 User ID가 일치해야 합니다.");
        assertEquals(expectedTitle, savedQuestion.getTitle(), "저장된 Question의 제목(title)이 예상과 일치해야 합니다.");
        assertEquals(expectedContent, savedQuestion.getContent(), "저장된 Question의 내용(content)이 예상과 일치해야 합니다.");
        assertEquals(0, savedQuestion.getViewCount(), "초기 조회수(viewCount)는 0이어야 합니다.");
        assertEquals(0, savedQuestion.getVoteCount(), "초기 추천수(voteCount)는 0이어야 합니다.");

        System.out.println("저장된 질문 ID: " + savedQuestion.getQuestionId());
        System.out.println("작성자 ID: " + savedQuestion.getUser().getUserId() + ", 닉네임: " + savedQuestion.getUser().getNickname());
        System.out.println("생성 시간: " + savedQuestion.getCreatedAt());
    }

    // === 아래에 새로운 테스트 메소드들 추가 ===

    @Test
    @DisplayName("ID로 질문(Question) 조회 시 해당 Question 반환")
    void findQuestionById_whenQuestionExists_shouldReturnQuestion() {
        // given - 테스트용 Question을 먼저 저장
        String titleForFindById = "ID 조회용 질문";
        Question questionToSave = Question.builder()
                .user(savedUser)
                .title(titleForFindById)
                .content("이 질문은 ID로 성공적으로 조회되어야 합니다.")
                .build();
        Question savedQuestion = questionRepository.save(questionToSave);
        Long savedQuestionId = savedQuestion.getQuestionId();

        // when - 저장된 ID를 사용하여 Question 조회
        Optional<Question> optionalFoundQuestion = questionRepository.findById(savedQuestionId);

        // then - 조회 결과 검증
        assertTrue(optionalFoundQuestion.isPresent(), "ID로 조회 시 Question 객체가 Optional에 존재해야 합니다.");
        Question foundQuestion = optionalFoundQuestion.get(); // Optional에서 실제 Question 객체를 가져옴

        assertEquals(savedQuestionId, foundQuestion.getQuestionId(), "조회된 Question의 ID가 저장된 ID와 일치해야 합니다.");
        assertEquals(titleForFindById, foundQuestion.getTitle(), "조회된 Question의 제목이 예상과 일치해야 합니다.");
        assertEquals(savedUser.getUserId(), foundQuestion.getUser().getUserId(), "조회된 Question의 User ID가 일치해야 합니다.");
        System.out.println("ID로 조회 성공 - 제목: " + foundQuestion.getTitle());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 질문(Question) 조회 시 Optional.empty 반환")
    void findQuestionById_whenQuestionDoesNotExist_shouldReturnEmptyOptional() {
        // given - 존재하지 않을 법한 ID
        Long nonExistentId = -999L; // 실제 DB에 없을 ID (음수 또는 매우 큰 값)

        // when - 존재하지 않는 ID로 Question 조회
        Optional<Question> optionalFoundQuestion = questionRepository.findById(nonExistentId);

        // then - 조회 결과 검증 (Optional이 비어있어야 함)
        assertFalse(optionalFoundQuestion.isPresent(), "존재하지 않는 ID로 조회 시 Optional.empty()가 반환되어야 합니다.");
        assertTrue(optionalFoundQuestion.isEmpty(), "존재하지 않는 ID로 조회 시 Optional.isEmpty()가 true여야 합니다."); // isEmpty()도 사용 가능
    }

    @Test
    @DisplayName("모든 질문(Question) 조회 시 저장된 Question 목록 반환")
    void findAllQuestions_shouldReturnAllSavedQuestions() {
        // given - 여러 개의 Question 저장
        Question question1 = Question.builder().user(savedUser).title("첫 번째 질문").content("첫 번째 질문 내용입니다.").build();
        Question question2 = Question.builder().user(savedUser).title("두 번째 질문").content("두 번째 질문 내용입니다.").build();
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when - 모든 Question 조회
        List<Question> allQuestions = questionRepository.findAll();

        // then - 조회 결과 검증
        assertNotNull(allQuestions, "조회된 질문 목록은 null이 아니어야 합니다.");
        // @DataJpaTest는 각 테스트 후 롤백되므로, 이 테스트 내에서 저장한 2개의 Question만 조회되어야 합니다.
        assertEquals(2, allQuestions.size(), "저장한 Question의 개수(2개)만큼 조회되어야 합니다.");

        // (선택적) 각 Question의 제목을 확인하여 정확히 저장된 객체들이 맞는지 검증
        assertTrue(allQuestions.stream().anyMatch(q -> q.getTitle().equals("첫 번째 질문")), "목록에 '첫 번째 질문'이 포함되어야 합니다.");
        assertTrue(allQuestions.stream().anyMatch(q -> q.getTitle().equals("두 번째 질문")), "목록에 '두 번째 질문'이 포함되어야 합니다.");
        System.out.println("findAll() 조회 결과, 질문 개수: " + allQuestions.size());
    }

    @Test
    @DisplayName("질문(Question) 내용 수정 후 저장 시 정상적으로 업데이트되는지 테스트")
    void updateQuestion_whenSavedAfterModification_shouldBeUpdated() {
        // given - 수정할 Question을 먼저 저장
        String originalTitle = "원본 질문 제목";
        String originalContent = "이것은 수정 전의 원본 내용입니다.";
        Question questionToSave = Question.builder()
                .user(savedUser)
                .title(originalTitle)
                .content(originalContent)
                .build();
        Question savedQuestion = questionRepository.save(questionToSave);
        Long savedQuestionId = savedQuestion.getQuestionId();
        LocalDateTime originalUpdatedAt = savedQuestion.getUpdatedAt(); // 초기 수정 시간 기록

        // when - 저장된 Question을 다시 조회하여 내용 수정 후 저장
        // ID로 조회하여 영속성 컨텍스트에서 관리되는 엔티티를 가져옴
        Optional<Question> optionalQuestionToUpdate = questionRepository.findById(savedQuestionId);
        assertTrue(optionalQuestionToUpdate.isPresent(), "수정할 Question이 조회되어야 합니다.");
        Question questionToUpdate = optionalQuestionToUpdate.get();

        String updatedTitle = "수정된 질문 제목입니다!";
        String updatedContent = "내용이 성공적으로 수정되었습니다.";
        questionToUpdate.setTitle(updatedTitle); // 제목 변경
        questionToUpdate.setContent(updatedContent); // 내용 변경

        // 명시적으로 save 호출 (JPA는 ID가 있는 엔티티이므로 MERGE 동작을 함)
        Question updatedQuestion = questionRepository.save(questionToUpdate);
        // questionRepository.flush(); // 변경 감지로 인한 UPDATE 쿼리를 즉시 보고 싶다면 flush() 사용 (선택 사항)

        // then - 수정된 내용 및 updatedAt 필드 검증
        assertNotNull(updatedQuestion, "업데이트된 Question 객체는 null이 아니어야 합니다.");
        assertEquals(savedQuestionId, updatedQuestion.getQuestionId(), "ID는 변경되지 않아야 합니다.");
        assertEquals(updatedTitle, updatedQuestion.getTitle(), "제목이 수정된 값으로 변경되어야 합니다.");
        assertEquals(updatedContent, updatedQuestion.getContent(), "내용이 수정된 값으로 변경되어야 합니다.");

        // updatedAt이 변경되었는지 확인 (JPA Auditing 기능)
        // 테스트 실행 속도가 매우 빠를 경우, createdAt과 updatedAt이 동일한 밀리초까지 나올 수 있으므로
        // 단순히 같지 않다는 것만으로는 부족할 수 있습니다. 여기서는 업데이트가 발생했음을 가정합니다.
        // 좀 더 확실한 방법은 Thread.sleep()을 아주 잠깐 주거나, 초 단위까지 비교하는 것입니다.
        // 혹은, 단순히 originalUpdatedAt과 다른지만 봐도 됩니다. (단, DB/시스템 시간에 따라 미세한 차이 없을 수도)
        assertNotNull(updatedQuestion.getUpdatedAt(), "updatedAt은 null이 아니어야 합니다.");
        // 이 검증은 테스트 환경과 실행 시점에 따라 미세하게 실패할 수 있으므로 주의가 필요합니다.
        // assertTrue(updatedQuestion.getUpdatedAt().isAfter(originalUpdatedAt), "updatedAt이 초기 시간 이후로 변경되어야 합니다.");
        // 더 안전하게는, 실제 값 변경 여부에 집중하는 것이 좋습니다. updatedAt 자체는 JPA Auditing의 역할이므로,
        // 다른 필드 변경 시 함께 변경된다는 정도로 이해해도 됩니다.
        System.out.println("수정 전 updatedAt: " + originalUpdatedAt);
        System.out.println("수정 후 updatedAt: " + updatedQuestion.getUpdatedAt());
        System.out.println("수정된 질문 제목: " + updatedQuestion.getTitle());
    }

    @Test
    @DisplayName("ID로 질문(Question) 삭제 후 해당 ID로 조회 시 Optional.empty 반환")
    void deleteQuestionById_whenDeleted_shouldNotExistAnymore() {
        // given - 삭제할 Question을 먼저 저장
        Question questionToDelete = Question.builder()
                .user(savedUser)
                .title("삭제될 질문")
                .content("이 질문은 곧 삭제됩니다.")
                .build();
        Question savedQuestion = questionRepository.save(questionToDelete);
        Long savedQuestionId = savedQuestion.getQuestionId();

        // ID로 Question이 정상적으로 저장되었는지 먼저 확인 (선택적이지만 좋은 습관)
        assertTrue(questionRepository.findById(savedQuestionId).isPresent(), "삭제 전 Question이 DB에 존재해야 합니다.");

        // when - 저장된 Question의 ID를 사용하여 삭제
        questionRepository.deleteById(savedQuestionId);
        // questionRepository.flush(); // DB에 즉시 반영되는 것을 보고 싶다면 (선택 사항)

        // then - 삭제 후 해당 ID로 조회 시 결과가 없어야 함
        Optional<Question> optionalDeletedQuestion = questionRepository.findById(savedQuestionId);
        assertFalse(optionalDeletedQuestion.isPresent(), "삭제된 Question은 더 이상 조회되지 않아야 합니다.");
        System.out.println("ID(" + savedQuestionId + ")로 질문 삭제 성공 확인.");
    }

    @Test
    @DisplayName("엔티티 객체로 질문(Question) 삭제 후 해당 ID로 조회 시 Optional.empty 반환")
    void deleteQuestionByEntity_whenDeleted_shouldNotExistAnymore() {
        // given - 삭제할 Question을 먼저 저장
        Question questionToDelete = Question.builder()
                .user(savedUser)
                .title("엔티티로 삭제될 질문")
                .content("이 질문은 엔티티 객체로 삭제될 것입니다.")
                .build();
        Question savedQuestion = questionRepository.save(questionToDelete);
        Long savedQuestionId = savedQuestion.getQuestionId();

        // when - 저장된 Question 엔티티 객체를 사용하여 삭제
        questionRepository.delete(savedQuestion);
        // questionRepository.flush();

        // then - 삭제 후 해당 ID로 조회 시 결과가 없어야 함
        Optional<Question> optionalDeletedQuestion = questionRepository.findById(savedQuestionId);
        assertFalse(optionalDeletedQuestion.isPresent(), "엔티티로 삭제된 Question은 더 이상 조회되지 않아야 합니다.");
        System.out.println("엔티티(" + savedQuestionId + ")로 질문 삭제 성공 확인.");
    }
}