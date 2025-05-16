package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.service.CustomUserDetailsService;
import ureca.ureca_mini.user.service.KakaoService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           RedirectAttributes redirectAttributes) {
        try {
            log.info("[카카오 콜백] code = {}", code);


            String accessToken = kakaoService.getAccessTokenFromKakao(code);

            KakaoUserInfoResponseDto info = kakaoService.getUserInfo(accessToken);

            UserEntity user = kakaoService.processKakaoLogin(info);


            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/";

        } catch (RuntimeException e) {
            log.error("카카오 로그인 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login/page";
        }
    }
}

