package mh.project_one.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/qna")
@Controller
public class QuestionController {

    // questions 컨트롤러로 이동시켜야
    @GetMapping("/questions")
    public String mainPage() {
        return "/qna/questions";
    }
}
