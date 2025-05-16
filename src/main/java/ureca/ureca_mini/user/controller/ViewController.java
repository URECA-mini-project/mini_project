package ureca.ureca_mini.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String forwardRoot() {
        return "redirect:/login/page";
    }

    @GetMapping("/main")
    public String showMainPage() {
        return "main"; // templates/main.html
    }
}
