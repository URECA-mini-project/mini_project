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

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("joinDTO") JoinDTO joinDTO, Model model) {
        if (joinService.isEmailDuplicate(joinDTO.getEmail())) {
            model.addAttribute("error", "이미 가입된 이메일입니다.");
            return "signup";
        }
        joinService.join(joinDTO);
        return "redirect:/login/page";
    }
}
