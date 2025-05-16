package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.service.KakaoService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        try {
            log.info("[카카오 콜백] 받은 인가 코드: {}", code);

            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

            kakaoService.processKakaoLogin(userInfo); // 여기서 중복된 이메일이면 예외 발생 가능

            return "redirect:/main"; // 로그인 성공 시 홈으로 이동

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login/page"; // 에러 시 로그인 페이지로 이동 + 메시지 전달
        }
    }
}
