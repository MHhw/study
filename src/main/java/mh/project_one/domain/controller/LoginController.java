package mh.project_one.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(){
        return "/user/login";
    }

    // 로그인 성공 후 보여줄 메인 페이지 또는 다른 적절한 페이지로 변경
    @GetMapping("/")
    public String home(){
        return "redirect:/qna/questions";
    }

    // questions 컨트롤러로 이동시켜야
    @GetMapping("/qna/questions")
    public String mainPage() {
        return "/qna/questions";
    }
}
