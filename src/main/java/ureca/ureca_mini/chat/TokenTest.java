package ureca.ureca_mini.chat;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.user.jwt.JWTUtil;

@Component
@RequiredArgsConstructor
public class TokenTest {

    private final JWTUtil jwtUtil;

    @PostConstruct
    public void init() {
        String token = jwtUtil.createJwt("testuser", 1000 * 60 * 60); // 1시간짜리
        System.out.println("🧪 테스트용 JWT: Bearer " + token);
    }
}

