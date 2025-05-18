package ureca.ureca_mini.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.jwt.JWTUtil;
import ureca.ureca_mini.user.service.KakaoService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final JWTUtil jwtUtil;

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         HttpServletResponse response) throws IOException {
        try {
            log.info("[카카오 콜백] 받은 인가 코드: {}", code);

            // 1) 카카오에서 AccessToken, 사용자 정보 가져오기
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

            // 2) 회원 가입 또는 로그인 처리 (DB 저장 / 조회)
            String email = kakaoService.processKakaoLogin(userInfo);

            // 3) JWT 생성 (예: 1시간 유효)
            String jwt = jwtUtil.createJwt(email, 1000L * 60 * 60);

            // 4) 클라이언트로 스크립트 내려줘서 localStorage에 저장 후 홈('/')으로 리다이렉트
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("  localStorage.setItem('jwt', 'Bearer " + jwt + "');");
            out.println("  location.href = '/';");
            out.println("</script>");
            out.flush();

        } catch (RuntimeException e) {
            // 실패 시 로그인 페이지로
            String error = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("/login/page?error=" + error);
        }
    }
}
