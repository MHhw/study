package mh.project_one.domain.service;

import mh.project_one.domain.dto.response.question.QuestionDetailResponse;
import mh.project_one.domain.dto.response.question.QuestionSummaryResponse;
// 필요한 경우 다른 DTO나 페이징 관련 클래스 import
import mh.project_one.domain.dto.response.answer.AnswerResponse;
import mh.project_one.domain.dto.response.tag.TagResponse;
import org.springframework.data.domain.Page; // Spring Data의 Page 사용
import org.springframework.data.domain.PageImpl; // Page 구현체
import org.springframework.data.domain.Pageable; // 페이징 요청 정보
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionService {

    // 임시 데이터를 저장할 리스트 (애플리케이션 실행 동안만 유지)
    // 실제로는 DB에서 조회해야 하지만, 지금은 임시 데이터 소스로 사용합니다.
    private final List<QuestionDetailResponse> temporaryQuestionStore = new ArrayList<>();

    // 서비스 생성 시 임시 데이터 초기화 (더미 데이터 생성)
    public QuestionService() {
        initializeTemporaryData();
    }

    private void initializeTemporaryData() {
        // 태그 임시 데이터
        TagResponse tagJava = TagResponse.builder().id(1L).name("Java").build();
        TagResponse tagSpring = TagResponse.builder().id(2L).name("Spring Boot").build();
        TagResponse tagJpa = TagResponse.builder().id(3L).name("JPA").build();
        TagResponse tagHtml = TagResponse.builder().id(4L).name("HTML").build();


        // 답변 임시 데이터
        AnswerResponse answer1_1 = AnswerResponse.builder()
                .id(101L)
                .content("답변 내용 1입니다. JPA를 사용해보세요.")
                .authorId(2L)
                .authorNickname("답변자A")
                .voteCount(5)
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .chosen(false)
                .build();

        AnswerResponse answer1_2 = AnswerResponse.builder()
                .id(102L)
                .content("답변 내용 2입니다. Thymeleaf 문법을 확인해보세요.")
                .authorId(3L)
                .authorNickname("답변자B")
                .voteCount(2)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .chosen(true)
                .build();


        // QuestionDetailResponse 형태의 임시 게시글 데이터 생성
        temporaryQuestionStore.add(QuestionDetailResponse.builder()
                .id(1L)
                .title("Spring Boot에서 JPA 설정은 어떻게 하나요?")
                .content("## JPA 설정 문의 \n\n Spring Boot 프로젝트에서 JPA를 사용하여 데이터베이스 연동을 하려고 합니다. \n\n `application.yml` 파일 설정과 `build.gradle` 의존성 추가 방법에 대해 알려주세요. \n\n 추가적으로, 간단한 Entity 예제도 보여주시면 감사하겠습니다.")
                .authorId(1L)
                .authorNickname("질문자1")
                .viewCount(150)
                .voteCount(10)
                .tags(Arrays.asList(tagSpring, tagJpa))
                .answers(Arrays.asList(answer1_1, answer1_2))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(5))
                .build());

        temporaryQuestionStore.add(QuestionDetailResponse.builder()
                .id(2L)
                .title("Thymeleaf 사용법 질문입니다.")
                .content("### Thymeleaf 문법 \n\n HTML 템플릿에 서버에서 전달받은 데이터를 표시하고 싶습니다. \n\n 예를 들어, `th:text`, `th:if`, `th:each` 같은 속성들을 어떻게 사용하는지 예제 코드로 설명해주세요.")
                .authorId(2L)
                .authorNickname("개발새내기")
                .viewCount(78)
                .voteCount(5)
                .tags(Arrays.asList(tagHtml, tagSpring))
                .answers(new ArrayList<>()) // 답변 없는 게시글
                .createdAt(LocalDateTime.now().minusHours(3))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build());

        // 더 많은 임시 데이터 추가 (페이징 테스트를 위해)
        for (int i = 3; i <= 25; i++) {
            temporaryQuestionStore.add(QuestionDetailResponse.builder()
                    .id((long) i)
                    .title("임시 질문 제목 " + i)
                    .content("임시 질문 내용입니다. " + i + "번째 질문입니다. \n\n Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                    .authorId((long) (i % 5) + 1) // 1~5번 유저가 번갈아 작성
                    .authorNickname("사용자" + ((long) (i % 5) + 1))
                    .viewCount(i * 10)
                    .voteCount(i % 5)
                    .tags(Arrays.asList(tagJava, i % 2 == 0 ? tagSpring : tagHtml))
                    .answers(new ArrayList<>())
                    .createdAt(LocalDateTime.now().minusDays(i).plusHours(i))
                    .updatedAt(LocalDateTime.now().minusDays(i).plusHours(i + 2))
                    .build());
        }
    }

    /**
     * 모든 게시글 목록을 페이징 처리하여 조회합니다. (임시 데이터 사용)
     * @param pageable 페이징 정보 (page, size, sort)
     * @return 페이징된 QuestionSummaryResponse 목록
     */
    public Page<QuestionSummaryResponse> findAllQuestions(Pageable pageable) {
        // QuestionDetailResponse 리스트를 QuestionSummaryResponse 리스트로 변환
        List<QuestionSummaryResponse> summaries = temporaryQuestionStore.stream()
                .map(detail -> QuestionSummaryResponse.builder()
                        .id(detail.getId())
                        .title(detail.getTitle())
                        .authorNickname(detail.getAuthorNickname())
                        .answerCount(detail.getAnswers() != null ? detail.getAnswers().size() : 0)
                        .viewCount(detail.getViewCount())
                        .voteCount(detail.getVoteCount())
                        .tags(detail.getTags().stream().map(TagResponse::getName).collect(Collectors.toList()))
                        .createdAt(detail.getCreatedAt())
                        .updatedAt(detail.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), summaries.size());

        List<QuestionSummaryResponse> pagedSummaries;
        if (start > summaries.size()) { // 요청한 페이지가 전체 데이터 범위를 벗어나는 경우 빈 리스트 반환
            pagedSummaries = List.of();
        } else {
            pagedSummaries = summaries.subList(start, end);
        }

        return new PageImpl<>(pagedSummaries, pageable, summaries.size());
    }

    /**
     * 특정 ID의 게시글 상세 정보를 조회합니다. (임시 데이터 사용)
     * @param id 게시글 ID
     * @return Optional<QuestionDetailResponse>
     */
    public Optional<QuestionDetailResponse> findQuestionById(Long id) {
        // 임시 저장소에서 ID로 게시글 찾기
        return temporaryQuestionStore.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst();
    }

    // TODO: 게시글 생성, 수정, 삭제 관련 메서드 추가 (현재는 기본 구조만)
    // public QuestionDetailResponse createQuestion(QuestionCreateRequest request, UserPrincipal currentUser) {
    //     // 임시로 ID 생성 (실제로는 DB가 생성)
    //     Long newId = temporaryQuestionStore.stream().mapToLong(QuestionDetailResponse::getId).max().orElse(0L) + 1;
    //     QuestionDetailResponse newQuestion = QuestionDetailResponse.builder()
    //             .id(newId)
    //             .title(request.getTitle())
    //             .content(request.getContent())
    //             .authorId(currentUser.getId())
    //             .authorNickname(currentUser.getNickname())
    //             .viewCount(0)
    //             .voteCount(0)
    //             .tags(new ArrayList<>()) // 태그 처리 로직 필요
    //             .answers(new ArrayList<>())
    //             .createdAt(LocalDateTime.now())
    //             .updatedAt(LocalDateTime.now())
    //             .build();
    //     temporaryQuestionStore.add(newQuestion);
    //     return newQuestion;
    // }
}