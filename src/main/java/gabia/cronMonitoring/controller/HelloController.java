package gabia.cronMonitoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) { //model 에  데이터를 심어서 뷰로 보낸다
        model.addAttribute("data", "hello님adsfsd님!!!");
        return "hello"; // 리턴은 화면 이름임
    }
}
