package mh.project_one.domain.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/product")
@Controller
public class ProductController {

    @ResponseBody
    @GetMapping("view")
    public String view(){

        return "test";
    }

}
