package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.jwt.JWTUtil;
import ureca.ureca_mini.user.service.KakaoService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController  // @Controller → @RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final JWTUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam("code") String code) {
        log.info("[카카오 콜백] 받은 인가 코드: {}", code);

        // 1) 카카오에서 AccessToken, 사용자 정보 가져오기
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // 2) 회원가입 또는 로그인 처리
        String email = kakaoService.processKakaoLogin(userInfo);

        // 3) JWT 생성
        String jwt = jwtUtil.createJwt(email, 1000L * 60 * 60);

        // 4) JSON으로 응답
        Map<String, String> result = new HashMap<>();
        result.put("token", jwt);

        return ResponseEntity.ok(result);
    }
}
