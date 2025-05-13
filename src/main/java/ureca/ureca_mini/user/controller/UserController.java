package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.service.JoinService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final JoinService joinService;

    // 로그인 화면
    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    // 회원가입 화면
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String processSignup(
            @ModelAttribute("joinDTO") JoinDTO joinDTO,
            Model model
    ) {
        // 중복 검사
        if (joinService.isDuplicate(joinDTO)) {
            model.addAttribute("error", "Email 또는 Username이 이미 사용 중입니다.");
            return "signup";
        }

        joinService.join(joinDTO);
        return "redirect:/";
    }
}
