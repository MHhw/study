package mh.project_one.domain.controller;

import mh.project_one.domain.dto.request.question.QuestionCreateRequest;
import mh.project_one.domain.dto.response.question.QuestionDetailResponse;
import mh.project_one.domain.dto.response.question.QuestionSummaryResponse;
// import mh.project_one.domain.dto.request.question.QuestionCreateRequest; // 게시글 생성 시 필요
// import mh.project_one.domain.dto.request.question.QuestionUpdateRequest; // 게시글 수정 시 필요
import mh.project_one.domain.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // 게시글 생성/수정 시 POST 사용 시
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.validation.BindingResult; // 유효성 검증 시 필요
// import jakarta.validation.Valid; // 유효성 검증 시 필요

import java.util.Optional;

@RequestMapping("/questions")
@Controller
public class QuestionController {

    private final QuestionService questionService;

    // 생성자 주입
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 게시글 목록 페이지를 반환합니다.
     * @param pageable 페이징 정보 (기본값: 페이지 0, 사이즈 10, ID 내림차순 정렬)
     * @param model 뷰에 전달할 데이터를 담는 객체
     * @return 게시글 목록 뷰 템플릿 경로
     */
    @GetMapping("/view")
    public String listQuestions(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<QuestionSummaryResponse> questionPage = questionService.findAllQuestions(pageable);
        model.addAttribute("questionPage", questionPage);
        // 페이징 처리를 위한 추가 정보 (선택 사항, Thymeleaf에서 계산 가능)
        // model.addAttribute("currentPage", questionPage.getNumber());
        // model.addAttribute("totalPages", questionPage.getTotalPages());
        // model.addAttribute("totalItems", questionPage.getTotalElements());
        return "qna/questions"; // resources/templates/qna/questions.html
    }

    /**
     * 특정 게시글 상세 페이지를 반환합니다.
     * @param questionId 조회할 게시글의 ID
     * @param model 뷰에 전달할 데이터를 담는 객체
     * @return 게시글 상세 뷰 템플릿 경로 또는 에러 페이지
     */
    @GetMapping("/{questionId}")
    public String detailQuestion(@PathVariable("questionId") Long questionId, Model model) {
        Optional<QuestionDetailResponse> questionOptional = questionService.findQuestionById(questionId);

        if (questionOptional.isPresent()) {
            model.addAttribute("question", questionOptional.get());
            return "qna/question-detail"; // resources/templates/qna/question-detail.html
        } else {
            // 게시글이 없을 경우, 404 에러 페이지나 다른 적절한 페이지로 리다이렉트
            // 여기서는 간단히 에러 템플릿을 반환하거나, 예외를 발생시켜 GlobalExceptionHandler가 처리하도록 할 수 있습니다.
            model.addAttribute("errorMessage", "요청하신 ID의 게시글을 찾을 수 없습니다: " + questionId);
            return "error"; // resources/templates/error.html (기본 에러 페이지)
        }
    }

    /**
     * 게시글 작성 폼 페이지를 반환합니다.
     * @param model 뷰에 전달할 데이터를 담는 객체
     * @return 게시글 작성 폼 뷰 템플릿 경로
     */
    @GetMapping("/new")
    public String newQuestionForm(Model model) {
        model.addAttribute("formObject", new QuestionCreateRequest());
        model.addAttribute("formAction", "/questions"); // 폼 제출 경로
        model.addAttribute("isEditMode", false);       // 작성 모드임을 명시
        model.addAttribute("pageTitle", "새 게시글 작성"); // 페이지 제목

        return "qna/question-form"; // resources/templates/qna/question-form.html
    }

    // --- 아래는 게시글 생성, 수정, 삭제를 위한 기본 구조 (추후 구현) ---

    /*
    @PostMapping
    public String createQuestion(@Valid @ModelAttribute QuestionCreateRequest request,
                                 BindingResult bindingResult,
                                 // Principal principal, // 현재 로그인 사용자 정보
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // model.addAttribute("questionCreateRequest", request); // 입력값 유지
            return "qna/question-form"; // 오류 시 다시 작성 폼으로
        }
        // UserPrincipal currentUser = ... // principal로부터 사용자 정보 가져오기
        // QuestionDetailResponse createdQuestion = questionService.createQuestion(request, currentUser);
        // return "redirect:/questions/" + createdQuestion.getId(); // 생성된 게시글 상세로 리다이렉트
        return "redirect:/questions"; // 임시로 목록으로 리다이렉트
    }
    */

    /*
    @GetMapping("/{questionId}/edit")
    public String editQuestionForm(@PathVariable("questionId") Long questionId, Model model) {
        Optional<QuestionDetailResponse> questionOptional = questionService.findQuestionById(questionId);
        if (questionOptional.isPresent()) {
            QuestionDetailResponse question = questionOptional.get();
            // 폼 바인딩을 위해 QuestionUpdateRequest DTO로 변환하거나 QuestionDetailResponse 그대로 사용
            // model.addAttribute("questionUpdateRequest", new QuestionUpdateRequest(question.getTitle(), question.getContent(), ...));
            model.addAttribute("question", question); // 기존 데이터를 폼에 채우기 위해 전달
            model.addAttribute("formAction", "/questions/" + questionId + "/edit"); // 폼 action 경로
            return "qna/question-form"; // 작성/수정 폼 공유 시
        } else {
            model.addAttribute("errorMessage", "수정할 게시글을 찾을 수 없습니다: " + questionId);
            return "error";
        }
    }
    */

    /*
    @PostMapping("/{questionId}/edit") // 또는 @PutMapping("/{questionId}")
    public String updateQuestion(@PathVariable("questionId") Long questionId,
                                 @Valid @ModelAttribute QuestionUpdateRequest request, // QuestionUpdateRequest DTO 사용
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // model.addAttribute("questionUpdateRequest", request);
            // model.addAttribute("question", questionService.findQuestionById(questionId).orElse(null)); // 폼 배경용 기존 데이터
            // model.addAttribute("formAction", "/questions/" + questionId + "/edit");
            return "qna/question-form";
        }
        // questionService.updateQuestion(questionId, request, ...);
        return "redirect:/questions/" + questionId; // 수정된 게시글 상세로 리다이렉트
    }
    */

    /*
    @PostMapping("/{questionId}/delete") // 또는 @DeleteMapping("/{questionId}")
    public String deleteQuestion(@PathVariable("questionId") Long questionId) {
        // questionService.deleteQuestion(questionId, ...);
        return "redirect:/questions"; // 목록으로 리다이렉트
    }
    */
}
