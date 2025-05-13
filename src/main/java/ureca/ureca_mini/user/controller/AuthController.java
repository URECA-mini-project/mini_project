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
        // 이메일 또는 사용자명 중복 체크
        if (joinService.isDuplicate(dto)) {
            return ResponseEntity
                    .status(409)
                    .body("Email or username already in use");
        }
        // 회원 정보 저장
        joinService.join(dto);
        return ResponseEntity.ok("Signup successful");
    }

}
