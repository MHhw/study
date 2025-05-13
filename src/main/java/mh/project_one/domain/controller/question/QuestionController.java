package mh.project_one.domain.controller.question;

import mh.project_one.domain.service.question.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/questions")
@Controller
public class QuestionController {

    private final QuestionService questionService;

    // 생성자 주입
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("view")
    public String view(){
        return "questions/view";
    }
}
