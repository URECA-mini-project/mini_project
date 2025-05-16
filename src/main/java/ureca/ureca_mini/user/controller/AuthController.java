package ureca.ureca_mini.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.service.JoinService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final JoinService joinService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody JoinDTO dto) {
        // 이메일 중복 체크만 수행
        if (joinService.isEmailDuplicate(dto.getEmail())) {
            return ResponseEntity
                    .status(409)
                    .body("이미 가입된 이메일입니다.");
        }

        // 회원 정보 저장
        joinService.join(dto);
        return ResponseEntity.ok("Signup successful");
    }

}
