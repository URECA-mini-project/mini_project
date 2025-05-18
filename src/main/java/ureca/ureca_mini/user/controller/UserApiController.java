package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.repository.UserRepository;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(Map.of(
                "email",    user.getEmail(),
                "username", user.getUsername(),
                "provider", user.getProvider()
        ));
    }
}
