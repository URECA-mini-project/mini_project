package ureca.ureca_mini.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // React 앱의 index.html 을 서빙
    @GetMapping({"/", "/login", "/signup"})
    public String forwardRoot() {
        return "forward:/index.html";
    }
}
