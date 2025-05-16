package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.service.KakaoService;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class KakaoLoginPageController {

    private final KakaoService kakaoService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;
    @GetMapping("/page")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + clientId + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);
        return "login";
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // 이미 가입된 이메일이면 로그인 페이지로 리다이렉트하면서 에러 메시지 전달
        if (kakaoService.isEmailAlreadyRegistered(userInfo.getKakaoAccount().getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 가입된 메일입니다.");
            return "redirect:/login/page";
        }

        // 로그인 처리
        kakaoService.processKakaoLogin(userInfo);
        return "redirect:/";
    }
}
